package com.sfin.message.messagegateway.service.impl;

import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.properties.ZaloOAProperty;
import com.sfin.message.messagegateway.repository.*;
import com.sfin.message.messagegateway.repository.entity.*;
import com.sfin.message.messagegateway.request.ShopTemplateRequest;
import com.sfin.message.messagegateway.response.*;
import com.sfin.message.messagegateway.service.ForwardService;
import com.sfin.message.messagegateway.service.ShopZaloService;
import com.sfin.message.messagegateway.service.ZaloService;
import com.sfin.message.messagegateway.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Log4j2
public class ShopZaloServiceImpl implements ShopZaloService {

    @Autowired
    private ShopZaloConfigDao shopZaloConfigDao;
    @Autowired
    private ShopTemplatesDao shopTemplatesDao;
    @Autowired
    private ZaloOAProperty zaloOAProperty;
    @Autowired
    private ForwardService forwardService;
    @Autowired
    private ZaloService zaloService;
    @Autowired
    private ZnsTemplateDetailDao znsTemplateDetailDao;
    @Autowired
    private HistorySendMessageDao historySendMessageDao;
    @Autowired
    private MessageDetailParamDao messageDetailParamDao;

    private String TEMPLATE_PATH = "?offset=%s&limit=%s";

    @Override
    public ResponseEntity getShopTemplateRegister(Long shopId, Integer status, Integer limit) {
        String path;
        if (status == null)
            path = String.format(TEMPLATE_PATH, 0, limit);
        else
            path = String.format(TEMPLATE_PATH + "&status=%s", 0, limit, status);
        ShopZaloConfigEntity zaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (zaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        HttpHeaders headers = forwardService.buildHeaders(zaloConfig.getAccessToken(), MediaType.APPLICATION_JSON);
        ResponseEntity response = forwardService.forward(zaloOAProperty.getTemplateUrl(), path, HttpMethod.GET, null, headers);
        if (response.getStatusCode().is2xxSuccessful()) {
            String body = (String) response.getBody();
            log.info("template response :{}", body);
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has("error")) {
                Integer error = jsonObject.getInt("error");
                if (error == 0) {
                    ShopTemplateResponse templateResponse = JsonUtils.jsonToObject(jsonObject.toString(), ShopTemplateResponse.class);
//                        for (DataTemplate template : templateResponse.getData()){
//                            TemplateDetailResponse templateDetail = zaloService.getDetailTemplate(template.getTemplateId(), shopId);
//                            if(templateDetail.getError() ==0)
//                                template.setDetailResponse(templateDetail);
//                            else
//                                throw new CoreException(CoreErrorCode.BAD_REQUEST, templateDetail.getMessage());
//                        }
                    return ResponseFactory.success(templateResponse);
                } else {
                    JsonUtils.returnErrorResponse(jsonObject.toString());
                }
            }
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity createShopTemplate(Long shopId, List<ShopTemplateRequest> shopTemplateRequests) {
        List<ShopTemplatesEntity> response = new ArrayList<>();
        for (ShopTemplateRequest request : shopTemplateRequests) {
            if(request.getTemplateId() == null)
                throw new CoreException(CoreErrorCode.BAD_REQUEST, "TemplateId không được rỗng");
            ShopTemplatesEntity shopTemplate = shopTemplatesDao.findByShopIdAndType(shopId, request.getType());
            if(shopTemplate == null){
                shopTemplate = new ShopTemplatesEntity();
                shopTemplate.setCreatedDate(new Date());
            }
            response.add(saveShopTemplate(shopTemplate, request, shopId));
        }
        return ResponseFactory.success(response);
    }

    @Override
    public ResponseEntity getTemplateOfShop(Long shopId, String keyword, Long begin, Long end, Boolean active, ShopTemplatesEntity.Type type, Pageable pageable) {
        Page<ShopTemplatesEntity> responses = shopTemplatesDao.findAll(makeQueryShopTemplate(keyword, shopId, begin, end, active, type), pageable).map(o -> {
            List<ZnsTemplateDetailEntity> details = znsTemplateDetailDao.findByTemplateId(o.getTemplateId());
            o.setTemplateDetails(details);
            return o;
        });
        return ResponseFactory.makePaginationResponse(responses);
    }

    @Override
    public ResponseEntity getTemplateDetailOfShop(Long shopId, Integer templateId) {
        ShopTemplatesEntity shopTemplate = shopTemplatesDao.findByShopIdAndTemplateId(shopId, templateId);
        if (shopTemplate == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        List<ZnsTemplateDetailEntity> znsTemplateDetails = znsTemplateDetailDao.findByTemplateId(shopTemplate.getTemplateId());
        shopTemplate.setTemplateDetails(znsTemplateDetails);
        return ResponseFactory.success(shopTemplate);
    }


    @Override
    public ResponseEntity deleteShopTemplate(Long shopTemplateId) {
        ShopTemplatesEntity shopTemplate = shopTemplatesDao.findById(shopTemplateId).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        shopTemplatesDao.delete(shopTemplate);
        return ResponseFactory.success();
    }

    @Override
    public ResponseEntity getHistorySendMessageZns(String keyword, Long begin, Long end, Long shopId, Integer templateId, Integer error, Pageable pageable) {
        Page<HistorySendMessageEntity> response = historySendMessageDao.findAll(makeQueryHistory(keyword, begin, end, shopId, templateId, error), pageable).map(o -> {
            List<MessageDetailParamEntity> params = messageDetailParamDao.findByHistoryMessageId(o.getId());
            o.setParams(params);
            return o;
        }) ;
        List<HistorySendMessageEntity> histories = historySendMessageDao.findAll(makeQueryHistory(keyword, begin, end, shopId, templateId, null));
        Map<String, Object> extraData = new HashMap<>();
        Float price = 0F;
        Integer success = 0, unSuccess = 0;
        for (HistorySendMessageEntity entity : histories) {
            if (price != null)
                price += entity.getPrice();
            if (entity.getError() != null && entity.getError() == 0)
                success += 1;
            else if (entity.getError() != null && entity.getError() != 0)
                unSuccess += 1;
        }
        extraData.put("price", price);
        extraData.put("success", success);
        extraData.put("unSuccess", unSuccess);
        return ResponseFactory.makePaginationResponse(response, extraData);
    }

    Specification<HistorySendMessageEntity> makeQueryHistory(String keyword, Long begin, Long end, Long shopId, Integer templateId, Integer error) {
        Specification<HistorySendMessageEntity> specification = Specification.where(null);
        String kw = "%" + keyword + "%";
        specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(HistorySendMessageEntity.HistorySendMessage_.TEMPLATE_NAME), kw)));
        if (begin != null && end != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(HistorySendMessageEntity.HistorySendMessage_.SEND_TIME), new Date(begin), new Date(end))));
        if (shopId != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(HistorySendMessageEntity.HistorySendMessage_.SHOP_ID), shopId)));
        if (templateId != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(HistorySendMessageEntity.HistorySendMessage_.TEMPLATE_ID), templateId)));
        if (error != null && error == 0)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(HistorySendMessageEntity.HistorySendMessage_.ERROR), 0)));
        else if (error != null && error != 0)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(HistorySendMessageEntity.HistorySendMessage_.ERROR), 0)));
        return specification;
    }

    Specification<ShopTemplatesEntity> makeQueryShopTemplate(String keyword, Long shopId, Long begin, Long end, Boolean active, ShopTemplatesEntity.Type type) {
        String kw = "%" + keyword + "%";
        Specification<ShopTemplatesEntity> specification = Specification.where(null);
        specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(ShopTemplatesEntity.ShopTemplate_.TEMPLATE_NAME), kw)));
        if (begin != null && end != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(ShopTemplatesEntity.ShopTemplate_.CREATE_DATE), new Date(begin), new Date(end))));
        if (shopId != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ShopTemplatesEntity.ShopTemplate_.SHOP_ID), shopId)));
        if (active != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ShopTemplatesEntity.ShopTemplate_.ACTIVE), active)));
        if (type != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ShopTemplatesEntity.ShopTemplate_.TYPE), type)));
        return specification;
    }

    private ShopTemplatesEntity saveShopTemplate(ShopTemplatesEntity shopTemplate, ShopTemplateRequest request, Long shopId){
        AppUtils.copyPropertiesIgnoreNull(request, shopTemplate);
        TemplateDetailResponse detailTemplate = zaloService.getDetailTemplate(request.getTemplateId(), shopId);
        if (detailTemplate.getError() != 0)
            throw new CoreException(CoreErrorCode.BAD_REQUEST, detailTemplate.getMessage());
        AppUtils.copyPropertiesIgnoreNull(detailTemplate.getData(), shopTemplate);
        shopTemplate.setPrice(Float.parseFloat(detailTemplate.getData().getPrice()));
        List<ZnsTemplateDetailEntity> znsTemplateDetails = znsTemplateDetailDao.findByTemplateId(shopTemplate.getTemplateId());
        znsTemplateDetailDao.deleteAll(znsTemplateDetails);
        for (TemplateParams templateData : detailTemplate.getData().getListParams()) {
            ZnsTemplateDetailEntity znsTemplateDetailEntity = new ZnsTemplateDetailEntity();
            znsTemplateDetailEntity.setTemplateId(request.getTemplateId());
            AppUtils.copyPropertiesIgnoreNull(templateData, znsTemplateDetailEntity);
            znsTemplateDetailEntity.setCreatedDate(new Date());
            znsTemplateDetailDao.save(znsTemplateDetailEntity);
        };
        return shopTemplatesDao.save(shopTemplate);
    }
}
