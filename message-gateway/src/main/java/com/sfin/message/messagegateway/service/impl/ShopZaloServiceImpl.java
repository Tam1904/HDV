package com.sfin.message.messagegateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.properties.ZaloOAProperty;
import com.sfin.message.messagegateway.repository.HistorySendMessageDao;
import com.sfin.message.messagegateway.repository.ShopTemplatesDao;
import com.sfin.message.messagegateway.repository.ShopZaloConfigDao;
import com.sfin.message.messagegateway.repository.ZnsTemplateDetailDao;
import com.sfin.message.messagegateway.repository.entity.HistorySendMessageEntity;
import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.repository.entity.ZnsTemplateDetailEntity;
import com.sfin.message.messagegateway.request.ShopTemplateRequest;
import com.sfin.message.messagegateway.request.TemplateShopRequest;
import com.sfin.message.messagegateway.request.UpdateShopTemplateRequest;
import com.sfin.message.messagegateway.response.*;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import com.sfin.message.messagegateway.service.ForwardService;
import com.sfin.message.messagegateway.service.ShopZaloService;
import com.sfin.message.messagegateway.service.ZaloService;
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
    private ObjectMapper objectMapper;
    @Autowired
    private ForwardService forwardService;
    @Autowired
    private ZaloService zaloService;
    @Autowired
    private ZnsTemplateDetailDao znsTemplateDetailDao;
    @Autowired
    private HistorySendMessageDao historySendMessageDao;

    private String TEMPLATE_PATH = "?offset=%s&limit=%s";

    @Override
    public ResponseEntity getShopTemplateRegister(Long shopId, Integer status, Integer limit) {
        String path ;
        if(status == null)
            path = String.format(TEMPLATE_PATH, 0, limit);
        else
            path = String.format(TEMPLATE_PATH + "&status=%s", 0, limit, status);
        ShopZaloConfigEntity zaloConfig = shopZaloConfigDao.findById(shopId).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        HttpHeaders headers = buildHttpHeaders(zaloConfig.getAccessToken());
        ResponseEntity response = forwardService.forward(zaloOAProperty.getTemplateUrl(), path, HttpMethod.GET, null, headers);
        if(response.getStatusCode().is2xxSuccessful()){
            try {
                String body = (String) response.getBody();
                log.info("template response :{}", body);
                JSONObject jsonObject = new JSONObject(body);
                if(jsonObject.has("error")){
                    Integer error = jsonObject.getInt("error");
                    if(error ==0){
                        ShopTemplateResponse templateResponse = objectMapper.readValue(jsonObject.toString(), new TypeReference<ShopTemplateResponse>() {
                        });
                        for (DataTemplate template : templateResponse.getData()){
                            TemplateDetailResponse templateDetail = zaloService.getDetailTemplate(template.getTemplateId(), shopId);
                            if(templateDetail.getError() ==0)
                                template.setDetailResponse(templateDetail);
                            else
                                throw new CoreException(CoreErrorCode.BAD_REQUEST, templateDetail.getMessage());
                        }
                        return ResponseFactory.success(templateResponse);
                    }
                    else {
                        ErrorResponse errorResponse = objectMapper.readValue(jsonObject.toString(), new TypeReference<ErrorResponse>() {
                        });
                        return ResponseFactory.success(errorResponse);
                    }
                }
            } catch (JsonProcessingException e) {
                log.info("error : {}", e.getMessage());
            }
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity createShopTemplate(Long shopId, TemplateShopRequest templateShopRequest){
        List<ShopTemplatesEntity> response = new ArrayList<>();
        for(ShopTemplateRequest request : templateShopRequest.getShopTemplate()){
            ShopTemplatesEntity shopTemplate = shopTemplatesDao.findByShopIdAndTemplateId(shopId, request.getTemplateId());
            if(shopTemplate != null)
                throw new CoreException(CoreErrorCode.ENTITY_EXISTED);
            shopTemplate = new ShopTemplatesEntity();
            shopTemplate.setShopId(shopId);
            AppUtils.copyPropertiesIgnoreNull(request, shopTemplate);
            TemplateDetailResponse detailTemplate = zaloService.getDetailTemplate(request.getTemplateId(), shopId);
            if(detailTemplate.getError()!= 0)
                throw new CoreException(CoreErrorCode.BAD_REQUEST, detailTemplate.getMessage());
            AppUtils.copyPropertiesIgnoreNull(detailTemplate.getData(), shopTemplate);
            shopTemplate.setPrice(Float.parseFloat(detailTemplate.getData().getPrice()));
            for(TemplateParams templateData : detailTemplate.getData().getListParams()){
                ZnsTemplateDetailEntity znsTemplateDetailEntity = new ZnsTemplateDetailEntity();
                znsTemplateDetailEntity.setTemplateId(request.getTemplateId());
                AppUtils.copyPropertiesIgnoreNull(templateData, znsTemplateDetailEntity);
                znsTemplateDetailEntity.setCreatedDate(new Date());
                znsTemplateDetailDao.save(znsTemplateDetailEntity);
            }
            shopTemplate.setCreatedDate(new Date());
            response.add(shopTemplatesDao.save(shopTemplate));
        }
        return ResponseFactory.success(response);
    }

    @Override
    public ResponseEntity getTemplateOfShop(Long shopId, String keyword, Long begin, Long end, Boolean active, Pageable pageable){
        Page<ShopTemplatesEntity> responses = shopTemplatesDao.findAll(makeQueryShopTemplate(keyword, shopId, begin, end, active), pageable).map(o -> {
            List<ZnsTemplateDetailEntity> details = znsTemplateDetailDao.findByTemplateId(o.getTemplateId());
            o.setTemplateDetails(details);
            return o;
        });
        return ResponseFactory.makePaginationResponse(responses);
    }

    @Override
    public ResponseEntity getTemplateDetailOfShop(Long shopTemplateId){
        ShopTemplatesEntity shopTemplate = shopTemplatesDao.findById(shopTemplateId).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        if(shopTemplate != null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        List<ZnsTemplateDetailEntity> znsTemplateDetails = znsTemplateDetailDao.findByTemplateId(shopTemplate.getTemplateId());
        shopTemplate.setTemplateDetails(znsTemplateDetails);
        return ResponseFactory.success(shopTemplate);
    }


    public HttpHeaders buildHttpHeaders(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("access_token", accessToken);
        return headers;
    }

    @Override
    public ResponseEntity updateTemplateShop(Long shopTemplateId, UpdateShopTemplateRequest request){
            ShopTemplatesEntity shopTemplate = shopTemplatesDao.findById(shopTemplateId).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
            AppUtils.copyPropertiesIgnoreNull(request, shopTemplate);
            TemplateDetailResponse detailTemplate = zaloService.getDetailTemplate(shopTemplate.getTemplateId(), shopTemplate.getShopId());
            if(detailTemplate.getError()==0){
                AppUtils.copyPropertiesIgnoreNull(detailTemplate.getData(), shopTemplate);
                shopTemplate.setPrice(Float.parseFloat(detailTemplate.getData().getPrice()));
                List<ZnsTemplateDetailEntity> znsTemplateDetails = znsTemplateDetailDao.findByTemplateId(shopTemplate.getTemplateId());
                znsTemplateDetailDao.deleteAll(znsTemplateDetails);
                for(TemplateParams templateData : detailTemplate.getData().getListParams()){
                    ZnsTemplateDetailEntity znsTemplateDetail = new ZnsTemplateDetailEntity();
                    znsTemplateDetail.setTemplateId(shopTemplate.getTemplateId());
                    AppUtils.copyPropertiesIgnoreNull(templateData, znsTemplateDetail);
                    znsTemplateDetail.setCreatedDate(new Date(System.currentTimeMillis()));
                    znsTemplateDetailDao.save(znsTemplateDetail);
                }
                shopTemplate.setCreatedDate(new Date());
            }
            throw new CoreException(CoreErrorCode.BAD_REQUEST, detailTemplate.getMessage());
    }


    @Override
    public ResponseEntity deleteShopTemplate(Long shopTemplateId) {
        ShopTemplatesEntity shopTemplate = shopTemplatesDao.findById(shopTemplateId).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        shopTemplatesDao.delete(shopTemplate);
        return ResponseFactory.success();
    }

    @Override
    public ResponseEntity getHistorySendMessageZns(String keyword, Long begin, Long end, Long shopId, Integer templateId, Integer error, Pageable pageable){
        Page<HistorySendMessageEntity> response = historySendMessageDao.findAll(makeQueryHistory(keyword, begin, end, shopId, templateId, error), pageable);
        List<HistorySendMessageEntity> histories = historySendMessageDao.findAll(makeQueryHistory(keyword, begin, end, shopId, templateId, null));
        Map<String , Object> extraData = new HashMap<>();
        Float price = 0F;
        Integer success = 0, unSuccess =0;
        for(HistorySendMessageEntity entity : histories){
            if(price!= null)
                price += entity.getPrice();
            if(entity.getError()!= null && entity.getError()==0)
                success +=1;
            else if(entity.getError()!= null && entity.getError() != 0)
                unSuccess +=1;
        }
        extraData.put("price", price);
        extraData.put("success", success);
        extraData.put("unSuccess", unSuccess);
        return ResponseFactory.makePaginationResponse(response, extraData);
    }

    Specification<HistorySendMessageEntity> makeQueryHistory(String keyword, Long begin, Long end, Long shopId, Integer templateId, Integer error){
        Specification<HistorySendMessageEntity> specification = Specification.where(null);
        String kw = "%" + keyword + "%";
        specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(HistorySendMessageEntity.HistorySendMessage_.TEMPLATE_NAME), keyword)));
        if(begin != null && end != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(HistorySendMessageEntity.HistorySendMessage_.SEND_TIME), new Date(begin), new Date(end))));
        if(shopId != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(HistorySendMessageEntity.HistorySendMessage_.SHOP_ID), shopId)));
        if(templateId != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(HistorySendMessageEntity.HistorySendMessage_.TEMPLATE_ID), templateId)));
        if(error != null && error == 0)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(HistorySendMessageEntity.HistorySendMessage_.ERROR), 0)));
        else if(error != null && error != 0)
            specification = specification.and(((root, query, criteriaBuilder) ->  criteriaBuilder.notEqual(root.get(HistorySendMessageEntity.HistorySendMessage_.ERROR), 0)));
        return specification;
    }

    Specification<ShopTemplatesEntity> makeQueryShopTemplate(String keyword, Long shopId, Long begin, Long end, Boolean active){
        String kw = "%" + keyword + "%";
        Specification<ShopTemplatesEntity> specification = Specification.where(null);
        specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(ShopTemplatesEntity.ShopTemplate_.TEMPLATE_NAME), kw)));
        if(begin != null && end != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(ShopTemplatesEntity.ShopTemplate_.CREATE_DATE), new Date(begin), new Date(end))));
        if(shopId!= null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ShopTemplatesEntity.ShopTemplate_.SHOP_ID), shopId)));
        if(active != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ShopTemplatesEntity.ShopTemplate_.ACTIVE), active)));
        return specification;
    }
}
