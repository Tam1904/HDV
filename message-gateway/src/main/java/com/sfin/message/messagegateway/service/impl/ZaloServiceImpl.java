package com.sfin.message.messagegateway.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.properties.ZaloOAProperty;
import com.sfin.message.messagegateway.repository.ShopTemplatesDao;
import com.sfin.message.messagegateway.repository.ShopZaloConfigDao;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.AccessTokenRequest;
import com.sfin.message.messagegateway.request.AuthorizationCodeRequest;
import com.sfin.message.messagegateway.request.ShopZaloConfigRequest;
import com.sfin.message.messagegateway.request.UpdateAccessTokenRequest;
import com.sfin.message.messagegateway.response.AccessTokenResponse;
import com.sfin.message.messagegateway.response.TemplateDetailResponse;
import com.sfin.message.messagegateway.response.error.AccessTokenErrorResponse;
import com.sfin.message.messagegateway.response.error.TemplateDetailError;
import com.sfin.message.messagegateway.service.ForwardService;
import com.sfin.message.messagegateway.service.ZaloService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

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
    private ShopTemplatesDao shopTemplatesDao;
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
        headers.add("secret_key", zaloConfig.getSecretKey());
        AccessTokenRequest request = AccessTokenRequest.builder()
                .code(zaloConfig.getAuthorizationCode())
                .appId(zaloConfig.getAppId())
                .grantType(AUTHORIZATION_CODE)
                .codeVerifier(zaloConfig.getCodeVerifier())
                .build();
        ResponseEntity response = forwardService.forward(zaloOAProperty.getAccessTokenUrl(), "", HttpMethod.POST, request, headers);
        Long currentTime = System.currentTimeMillis();
        zaloConfig = buildShopZaloConfig(response, zaloConfig, currentTime);
        zaloConfig.setRefreshTokenExpires(new Date(currentTime + 3 * 30 * 24 * 60 * 60 * 1000));
        return shopZaloConfigDao.save(zaloConfig);
    }

    @Override
    public ShopZaloConfigEntity updateAccessToken(ShopZaloConfigEntity zaloConfig) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("secret_key", zaloConfig.getSecretKey());
        UpdateAccessTokenRequest request = UpdateAccessTokenRequest.builder()
                .refreshToken(zaloConfig.getRefreshToken())
                .grantType(REFRESH_TOKEN)
                .appId(zaloConfig.getAppId())
                .build();
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        ResponseEntity response = forwardService.forward(zaloOAProperty.getAccessTokenUrl(), "", HttpMethod.POST, entity, headers);
        Long currentTime = System.currentTimeMillis();
        zaloConfig = buildShopZaloConfig(response, zaloConfig, currentTime);
        return shopZaloConfigDao.save(zaloConfig);
    }

    public ShopZaloConfigEntity buildShopZaloConfig(ResponseEntity response, ShopZaloConfigEntity zaloConfig, Long currentTime) {
        if (response.getStatusCode().is2xxSuccessful()) {
            Object data = response.getBody();
            if (data instanceof AccessTokenResponse) {
                AccessTokenResponse accessTokenResponse = forwardService.getValue(data, AccessTokenResponse.class);
                Long timeExpire = Long.valueOf(accessTokenResponse.getExpiresIn());
                zaloConfig.setAccessToken(accessTokenResponse.getAccessToken());
                zaloConfig.setRefreshToken(accessTokenResponse.getRefreshToken());
                zaloConfig.setAccessTokenExpires(new Date(currentTime + timeExpire * 1000));
                zaloConfig.setModifyTokenDate(new Date(currentTime));
                return zaloConfig;
            } else if (data instanceof AccessTokenErrorResponse) {
                AccessTokenErrorResponse accessTokenErrorResponse = forwardService.getValue(data, AccessTokenErrorResponse.class);
                Map<String, Object> mData = new HashMap<>();
                mData.put("error", accessTokenErrorResponse);
                throw new CoreException(CoreErrorCode.BAD_REQUEST, mData);
            }
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    public TemplateDetailResponse getDetailTemplate(Integer templateId, Long shopId) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findById(shopId).orElseThrow(() -> new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("access_token", shopZaloConfig.getAccessToken());
        String baseUrl = zaloOAProperty.getTemplateInfoUrl();
        String path = "?template_id=" + templateId;
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity response = forwardService.forward(baseUrl, path, HttpMethod.GET, entity, headers);
        if (response.getStatusCode().is2xxSuccessful()) {
            Object data = response.getBody();
            if (data instanceof TemplateDetailResponse) {
                TemplateDetailResponse detailResponse = forwardService.getValue(data, TemplateDetailResponse.class);
                return detailResponse;
            } else if (data instanceof TemplateDetailError) {
                TemplateDetailError templateDetailError = forwardService.getValue(data, TemplateDetailError.class);
                Map<String, Object> mData = new HashMap<>();
                mData.put("error", templateDetailError);
                throw new CoreException(CoreErrorCode.BAD_REQUEST, mData);
            }
        }
        throw new CoreException(CoreErrorCode.BAD_REQUEST);
    }

}
