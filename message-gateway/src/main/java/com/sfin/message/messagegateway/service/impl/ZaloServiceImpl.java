package com.sfin.message.messagegateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.eplaform.commons.utils.Definition;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.properties.ZaloOAProperty;
import com.sfin.message.messagegateway.repository.*;
import com.sfin.message.messagegateway.repository.entity.*;
import com.sfin.message.messagegateway.request.*;
import com.sfin.message.messagegateway.response.AccessTokenResponse;
import com.sfin.message.messagegateway.response.OaInfoResponse;
import com.sfin.message.messagegateway.response.SendMessageZNSResponse;
import com.sfin.message.messagegateway.response.TemplateDetailResponse;
import com.sfin.message.messagegateway.response.error.AccessTokenErrorResponse;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import com.sfin.message.messagegateway.service.ForwardService;
import com.sfin.message.messagegateway.service.UserOAService;
import com.sfin.message.messagegateway.service.ZaloService;
import com.sfin.message.messagegateway.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

@Service
@Log4j2
public class ZaloServiceImpl implements ZaloService {

    @Autowired
    private ShopZaloConfigDao shopZaloConfigDao;
    @Autowired
    private ZaloOAProperty zaloOAProperty;
    @Autowired
    private ForwardService forwardService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private HistorySendMessageDao historySendMessageDao;
    @Autowired
    private ShopTemplatesDao shopTemplatesDao;
    @Autowired
    private ZnsTemplateDetailDao znsTemplateDetailDao;
    @Autowired
    private UserOAService userOAService;
    @Autowired
    private MessageDetailParamDao messageDetailParamDao;

    private String AUTHORIZATION_CODE = "authorization_code";
    private String REFRESH_TOKEN = "refresh_token";


    @Override
    public ShopZaloConfigEntity createZaloOAConfig(ShopZaloConfigRequest request) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(request.getShopId());
        if (shopZaloConfig != null)
            throw new CoreException(CoreErrorCode.ENTITY_EXISTED);
        shopZaloConfig = new ShopZaloConfigEntity();
        AppUtils.copyPropertiesIgnoreNull(request, shopZaloConfig);
        shopZaloConfig.setCreatedDate(new Date());
        return shopZaloConfigDao.save(shopZaloConfig);
    }

    @Override
    public ShopZaloConfigEntity updateZaloOAConfig(Long id, UpdateShopZaloConfigRequest request) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findById(id).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        if (!shopZaloConfig.getOaId().equals(request.getOaId())) {
            ShopZaloConfigEntity entity = shopZaloConfigDao.findByShopIdAndOaId(shopZaloConfig.getShopId(), request.getOaId());
            if (entity != null)
                throw new CoreException(CoreErrorCode.ENTITY_EXISTED, "Tồn tại ShopId và OA-ID");
        }
        AppUtils.copyPropertiesIgnoreNull(request, shopZaloConfig);
        shopZaloConfig.setCreatedDate(new Date());
        return shopZaloConfigDao.save(shopZaloConfig);
    }

    @Override
    public ResponseEntity getOneShopConfig(Long shopId) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (shopZaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        return ResponseFactory.success(shopZaloConfig);
    }


    @Override
    public String generateUrlCode(Long shopId, String oaId) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findByShopIdAndOaId(shopId, oaId);
        if (shopZaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        String codeVerifier = genCodeVerifier();
        log.info("code verifier: {}", codeVerifier);
        String codeChallenge = genCodeChallenge(codeVerifier);
        log.info("code challenge: {}", codeChallenge);
        String authorizationCodeUrl = zaloOAProperty.getAuthorizationCodeUrl();
        String endPoint = String.format(authorizationCodeUrl, shopZaloConfig.getAppId(), zaloOAProperty.getRedirectUrl(), codeChallenge);
        log.info("endPoint authen code {}", endPoint);

        shopZaloConfig.setCodeChallenge(codeChallenge);
        shopZaloConfig.setCodeVerifier(codeVerifier);
        shopZaloConfigDao.save(shopZaloConfig);
        return endPoint;
    }

    public String genCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        String verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(code);
        return verifier;
    }

    public String genCodeChallenge(String codeVerifier) {
        String result = null;
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, 0, bytes.length);
            byte[] digest = md.digest();
            result = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    @Override
    public ShopZaloConfigEntity updateAuthorizationCode(AuthorizationCodeRequest request) {
        ShopZaloConfigEntity zaloConfig = shopZaloConfigDao.findByOaId(request.getOaId());
        if (zaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        if (!zaloConfig.getCodeChallenge().equals(request.getCodeChallenge()))
            throw new CoreException(CoreErrorCode.BAD_REQUEST);
        zaloConfig.setAuthorizationCode(request.getCode());
        return getAccessToken(shopZaloConfigDao.save(zaloConfig));
    }

    @Override
    public ShopZaloConfigEntity getAccessToken(ShopZaloConfigEntity zaloConfig) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(Definition.ZALO_CONFIG.SECRET_KEY, zaloConfig.getSecretKey());
        MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("code", zaloConfig.getAuthorizationCode());
        valueMap.add("app_id", zaloConfig.getAppId());
        valueMap.add("grant_type", AUTHORIZATION_CODE);
        valueMap.add("code_verifier", zaloConfig.getCodeVerifier());
        ResponseEntity response = forwardService.forward(zaloOAProperty.getAccessTokenUrl(), "", HttpMethod.POST, valueMap, headers);
        Long currentTime = System.currentTimeMillis();
        zaloConfig = buildShopZaloConfig(response, zaloConfig, currentTime);
        zaloConfig.setRefreshTokenExpires(new Date(currentTime + 3 * 30 * 24 * 60 * 60 * 1000));
        OaInfoResponse oaInfoResponse = userOAService.getOAInfo(zaloConfig.getShopId());
        AppUtils.copyPropertiesIgnoreNull(oaInfoResponse.getData(), zaloConfig);
        zaloConfig = shopZaloConfigDao.save(zaloConfig);
        userOAService.saveUserOa(zaloConfig.getShopId());
        return zaloConfig;
    }

    @Override
    public ShopZaloConfigEntity updateAccessToken(ShopZaloConfigEntity zaloConfig) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(Definition.ZALO_CONFIG.SECRET_KEY, zaloConfig.getSecretKey());

        MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("refresh_token", zaloConfig.getRefreshToken());
        valueMap.add("app_id", zaloConfig.getAppId());
        valueMap.add("grant_type", REFRESH_TOKEN);
        ResponseEntity response = forwardService.forward(zaloOAProperty.getAccessTokenUrl(), "", HttpMethod.POST, valueMap, headers);
        Long currentTime = System.currentTimeMillis();
        zaloConfig = buildShopZaloConfig(response, zaloConfig, currentTime);
        return shopZaloConfigDao.save(zaloConfig);
    }

    public ShopZaloConfigEntity buildShopZaloConfig(ResponseEntity response, ShopZaloConfigEntity zaloConfig, Long currentTime) {
        if (response.getStatusCode().is2xxSuccessful()) {
            String data = (String) response.getBody();
            JSONObject object = new JSONObject(data);
            if (object.has("access_token")) {
                AccessTokenResponse accessTokenResponse = JsonUtils.jsonToObject(object.toString(), AccessTokenResponse.class);
                Long timeExpire = Long.valueOf(accessTokenResponse.getExpiresIn());
                zaloConfig.setAccessToken(accessTokenResponse.getAccessToken());
                zaloConfig.setRefreshToken(accessTokenResponse.getRefreshToken());
                zaloConfig.setAccessTokenExpires(new Date(currentTime + timeExpire * 1000));
                zaloConfig.setModifyTokenDate(new Date(currentTime));
                return zaloConfig;
            } else if (object.has("error_name")) {
                JsonUtils.returnErrorResponse(object.toString());
            }
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    @Override
    public TemplateDetailResponse getDetailTemplate(Integer templateId, Long shopId) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (shopZaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        HttpHeaders headers = forwardService.buildHeaders(shopZaloConfig.getAccessToken(), MediaType.APPLICATION_JSON);
        String baseUrl = zaloOAProperty.getTemplateInfoUrl();
        String path = "?template_id=" + templateId;
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity response = forwardService.forward(baseUrl, path, HttpMethod.GET, entity, headers);
        if (response.getStatusCode().is2xxSuccessful()) {
            String data = (String) response.getBody();
            JSONObject object = new JSONObject(data);
            if (object.has("error")) {
                Integer errorCode = object.getInt("error");
                if (errorCode == 0) {
                    TemplateDetailResponse detailResponse = JsonUtils.jsonToObject(object.toString(), TemplateDetailResponse.class);
                    return detailResponse;
                } else {
                    JsonUtils.returnErrorResponse(object.toString());
                }
            }
        }
        throw new CoreException(CoreErrorCode.BAD_REQUEST);
    }

    @Override
    public void sendMessage(NotificationRequest request, String phone) {
        Long shopId = Long.valueOf((String) request.getData().get("shop_id"));
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (shopZaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        Integer templateId = Integer.valueOf((String) request.getData().get("template_id"));

        HttpHeaders headers = forwardService.buildHeaders(shopZaloConfig.getAccessToken(), MediaType.APPLICATION_JSON);

        JSONObject json = new JSONObject();
        json.put("phone", phone);
        json.put("mode", "development");
        json.put("template_id", request.getData().get("template_id"));
        String trackingId = RandomStringUtils.randomAlphabetic(20) + System.currentTimeMillis();
        json.put("tracking_id", trackingId);
        ShopTemplatesEntity shopTemplate = shopTemplatesDao.findByShopIdAndTemplateId(shopId, templateId);
        if (shopTemplate == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        List<ZnsTemplateDetailEntity> templateDetails = znsTemplateDetailDao.findByTemplateId(templateId);
        Map<String, Object> objectMap = new HashMap<>();
        List<MessageDetailParamEntity> messageDetailParams = new ArrayList<>();
        for (ZnsTemplateDetailEntity param : templateDetails){
            objectMap.put(param.getName(), request.getData().getOrDefault(param.getName(), ""));
            MessageDetailParamEntity entity = MessageDetailParamEntity.builder()
                    .createdDate(new Date())
                    .paramName(param.getName())
                    .templateId(templateId)
                    .paramValue(String.valueOf(request.getData().getOrDefault(param.getName(), "")))
                    .parmaType(param.getType())
                    .build();
            messageDetailParams.add(entity);
        }
        json.put("template_data", objectMap);
        log.info("send message with templateId {} and body {}", templateId, json);

        ResponseEntity response = forwardService.forward(zaloOAProperty.getZnsUrl(), "", HttpMethod.POST, json.toString(), headers);
        log.info("response call zns {}", response.getBody());
        try {
            String data = (String) response.getBody();
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("error")) {
                HistorySendMessageEntity historySendMessageEntity = new HistorySendMessageEntity();
                historySendMessageEntity.setShopId(shopId);
                historySendMessageEntity.setTemplateId(templateId);
                historySendMessageEntity.setBody(json.toString());
                historySendMessageEntity.setResponse(data);
                Integer error = jsonObject.getInt("error");
                if (error == 0) {
                    SendMessageZNSResponse znsResponse = JsonUtils.jsonToObject(jsonObject.toString(), SendMessageZNSResponse.class);
                    AppUtils.copyPropertiesIgnoreNull(znsResponse, historySendMessageEntity);
                    AppUtils.copyPropertiesIgnoreNull(znsResponse.getData(), historySendMessageEntity);
                    AppUtils.copyPropertiesIgnoreNull(znsResponse.getData().getQuota(), historySendMessageEntity);
                    AppUtils.copyPropertiesIgnoreNull(shopTemplate, historySendMessageEntity);
                    historySendMessageEntity.setTrackingId(trackingId);
                    historySendMessageEntity.setPhone(phone);
                    historySendMessageEntity.setPrice(shopTemplate.getPrice());
                } else {
                    ErrorResponse templateDetailError = objectMapper.readValue(data, new TypeReference<ErrorResponse>() {
                    });
                    historySendMessageEntity.setError(templateDetailError.getError());
                    historySendMessageEntity.setMessage(templateDetailError.getMessage());
                    historySendMessageEntity.setSendTime(new Date());
                }
                log.info("save history send zns with msg_id {}", historySendMessageEntity.getMsgId());
                historySendMessageEntity.setId(null);
                HistorySendMessageEntity entity = historySendMessageDao.save(historySendMessageEntity);
                messageDetailParams.forEach(o -> {
                    o.setHistoryMessageId(entity.getId());
                    o.setMsgId(entity.getMsgId());
                    messageDetailParamDao.save(o);
                });
            }
        } catch (Exception ex) {
            log.info("error send message {}", ex.getMessage());
        }
    }

    @Override
    public void addMessageToRedis(NotificationRequest request) {
        log.info("==== add message zns to queue {}", request);
        redisRepository.addMessageToQueue(request);
    }

}
