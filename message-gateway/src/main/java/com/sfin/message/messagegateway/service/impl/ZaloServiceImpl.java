package com.sfin.message.messagegateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.eplaform.commons.utils.Definition;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.properties.ZaloOAProperty;
import com.sfin.message.messagegateway.repository.HistorySendMessageDao;
import com.sfin.message.messagegateway.repository.RedisRepository;
import com.sfin.message.messagegateway.repository.ShopZaloConfigDao;
import com.sfin.message.messagegateway.repository.entity.HistorySendMessage;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.*;
import com.sfin.message.messagegateway.response.AccessTokenResponse;
import com.sfin.message.messagegateway.response.SendMessageZNSResponse;
import com.sfin.message.messagegateway.response.TemplateDetailResponse;
import com.sfin.message.messagegateway.response.TemplateParams;
import com.sfin.message.messagegateway.response.error.AccessTokenErrorResponse;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import com.sfin.message.messagegateway.service.ForwardService;
import com.sfin.message.messagegateway.service.ZaloService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    private String AUTHORIZATION_CODE = "authorization_code";
    private String REFRESH_TOKEN = "refresh_token";


    @Override
    public ShopZaloConfigEntity createZaloOAConfig(ShopZaloConfigRequest request) {
        ShopZaloConfigEntity shopZaloConfig = new ShopZaloConfigEntity();
        AppUtils.copyPropertiesIgnoreNull(request, shopZaloConfig);
        shopZaloConfig.setCreatedDate(new Date());
        return shopZaloConfigDao.save(shopZaloConfig);
    }


    @Override
    public String generateUrlCode(Long shopId) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findById(shopId).orElseThrow(() -> new CoreException(CoreErrorCode.BAD_REQUEST));
        String codeVerifier = RandomStringUtils.random(43, true, true);
        log.info("code verifier: {}", codeVerifier);
        String codeChallenge = Base64.getEncoder().withoutPadding().encodeToString(DigestUtils.sha256(codeVerifier));
        log.info("code challenge: {}", codeChallenge);
        String authorizationCodeUrl = zaloOAProperty.getAuthorizationCodeUrl();
        String endPoint = String.format(authorizationCodeUrl, shopZaloConfig.getAppId(), zaloOAProperty.getRedirectUrl(), codeChallenge);
        log.info("endPoint authen code {}", endPoint);

        shopZaloConfig.setCodeChallenge(codeChallenge);
        shopZaloConfig.setCodeVerifier(codeVerifier);
        shopZaloConfigDao.save(shopZaloConfig);
        return endPoint;
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
        return shopZaloConfigDao.save(zaloConfig);
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
        HttpEntity<Object> entity = new HttpEntity<>(valueMap, headers);
        ResponseEntity response = forwardService.forward(zaloOAProperty.getAccessTokenUrl(), "", HttpMethod.POST, entity, headers);
        Long currentTime = System.currentTimeMillis();
        zaloConfig = buildShopZaloConfig(response, zaloConfig, currentTime);
        return shopZaloConfigDao.save(zaloConfig);
    }

    public ShopZaloConfigEntity buildShopZaloConfig(ResponseEntity response, ShopZaloConfigEntity zaloConfig, Long currentTime) {
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                String data = (String) response.getBody();
                JSONObject object = new JSONObject(data);
                if (object.has("access_token")) {
                    AccessTokenResponse accessTokenResponse = objectMapper.readValue(object.toString(), new TypeReference<AccessTokenResponse>() {
                    });
                    Long timeExpire = Long.valueOf(accessTokenResponse.getExpiresIn());
                    zaloConfig.setAccessToken(accessTokenResponse.getAccessToken());
                    zaloConfig.setRefreshToken(accessTokenResponse.getRefreshToken());
                    zaloConfig.setAccessTokenExpires(new Date(currentTime + timeExpire * 1000));
                    zaloConfig.setModifyTokenDate(new Date(currentTime));
                    return zaloConfig;
                } else if (object.has("error_name")) {
                    AccessTokenErrorResponse accessTokenErrorResponse = objectMapper.readValue(object.toString(), new TypeReference<AccessTokenErrorResponse>() {
                    });
                    log.info("message code {}: {}", accessTokenErrorResponse.getError(), accessTokenErrorResponse.getErrorName());
                    Map<String, Object> mData = new HashMap<>();
                    mData.put("error", accessTokenErrorResponse);
                    throw new CoreException(CoreErrorCode.BAD_REQUEST, mData);
                }
            } catch (IOException ex) {
                log.info("error message get access token {}", ex.getMessage());
            }
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    public TemplateDetailResponse getDetailTemplate(Integer templateId, Long shopId) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findById(shopId).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(Definition.ZALO_CONFIG.ACCESS_TOKEN, shopZaloConfig.getAccessToken());
        String baseUrl = zaloOAProperty.getTemplateInfoUrl();
        String path = "?template_id=" + templateId;
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity response = forwardService.forward(baseUrl, path, HttpMethod.GET, entity, headers);
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                String data = (String) response.getBody();
                JSONObject object = new JSONObject(data);
                if (object.get("error") != null) {
                    Integer errorCode = object.getInt("error");
                    if (errorCode == 0) {
                        TemplateDetailResponse detailResponse = objectMapper.readValue(data, new TypeReference<TemplateDetailResponse>() {
                        });
                        return detailResponse;
                    } else {
                        ErrorResponse templateDetailError = objectMapper.readValue(data, new TypeReference<ErrorResponse>() {
                        });
                        Map<String, Object> mData = new HashMap<>();
                        mData.put("error", templateDetailError);
                        throw new CoreException(CoreErrorCode.BAD_REQUEST, mData);
                    }
                }
            } catch (JsonProcessingException ex) {
                log.info("error message get template id {}", ex.getMessage());
            }
        }
        throw new CoreException(CoreErrorCode.BAD_REQUEST);
    }

    @Override
    public void sendMessage(NotificationRequest request) {
        Long shopId = Long.valueOf((String) request.getData().get("shop_id"));
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findById(shopId)
                .orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        Integer templateId = Integer.valueOf((String) request.getData().get("template_id"));
        TemplateDetailResponse template = getDetailTemplate(templateId, shopId);
        log.info("template id {} : {}", templateId, template);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(Definition.ZALO_CONFIG.ACCESS_TOKEN, shopZaloConfig.getAccessToken());

        JSONObject json = new JSONObject();
        json.put("phone", request.getData().get("phone"));
        json.put("template_id", request.getData().get("template_id"));
        json.put("tracking_id", RandomStringUtils.randomAlphabetic(20) + System.currentTimeMillis());
        Map<String, Object> objectMap = new HashMap<>();
        for (TemplateParams params : template.getData().getListParams())
            objectMap.put(params.getName(), request.getData().getOrDefault(params.getName(), ""));
        json.put("template_data", objectMap);
        log.info("send message with templateId {} and body {}", templateId, json);

        ResponseEntity response = forwardService.forward(zaloOAProperty.getZnsUrl(), "", HttpMethod.POST, json.toString(), headers);
        log.info("response call zns {}", response.getBody());
        try {
            String data = (String) response.getBody();
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("error")) {
                HistorySendMessage historySendMessage = new HistorySendMessage();
                historySendMessage.setShopId(shopId);
                historySendMessage.setTemplateId(templateId);
                historySendMessage.setBody(jsonObject.toString());
                Integer error = jsonObject.getInt("error");
                log.info("error : {}", error);
                if (error == 0) {
                    SendMessageZNSResponse znsResponse = objectMapper.readValue(data, new TypeReference<SendMessageZNSResponse>() {
                    });
                    AppUtils.copyPropertiesIgnoreNull(znsResponse, historySendMessage);
                    AppUtils.copyPropertiesIgnoreNull(znsResponse.getData(), historySendMessage);
//                    Date sendTime = new Date(znsResponse.getData().getSendTime());
//                    historySendMessage.setSendTime(sendTime);
                    AppUtils.copyPropertiesIgnoreNull(znsResponse.getData().getQuota(), historySendMessage);
                } else {
                    ErrorResponse templateDetailError = objectMapper.readValue(data, new TypeReference<ErrorResponse>() {
                    });
                    historySendMessage.setError(templateDetailError.getError());
                    historySendMessage.setMessage(template.getMessage());
                    historySendMessage.setSendTime(new Date());
                }
                log.info("save history send zns with msg_id {}", historySendMessage.getMsgId());
                historySendMessageDao.save(historySendMessage);
            }
        } catch (Exception ex) {
            log.info("error send message {}", ex.getMessage());
        }
    }

    @Override
    public void addMessageToRedis(NotificationRequest request){
        log.info("==== add message zns to queue {}" , request);
        redisRepository.addMessageToQueue(request);
    }

}
