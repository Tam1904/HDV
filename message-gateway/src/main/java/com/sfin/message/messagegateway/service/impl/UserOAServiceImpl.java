package com.sfin.message.messagegateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.properties.ZaloOAProperty;
import com.sfin.message.messagegateway.repository.ShopZaloConfigDao;
import com.sfin.message.messagegateway.repository.UserZaloInfoDao;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.repository.entity.UserZaloInfoEntity;
import com.sfin.message.messagegateway.request.SendMessageRequest;
import com.sfin.message.messagegateway.response.*;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import com.sfin.message.messagegateway.service.ForwardService;
import com.sfin.message.messagegateway.service.UserOAService;
import com.sfin.message.messagegateway.utils.JsonUtils;
import lombok.SneakyThrows;
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
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class UserOAServiceImpl implements UserOAService {

    @Autowired
    private ZaloOAProperty zaloOAProperty;
    @Autowired
    private ForwardService forwardService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShopZaloConfigDao shopZaloConfigDao;
    @Autowired
    private UserZaloInfoDao userZaloInfoDao;
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public OaInfoResponse getOAInfo(Long shopId) {
        ShopZaloConfigEntity zaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (zaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        HttpHeaders headers = forwardService.buildHeaders(zaloConfig.getAccessToken(), MediaType.APPLICATION_JSON);
        ResponseEntity response = forwardService.forward(zaloOAProperty.getInfoUrl(), "", HttpMethod.GET, null, headers);
        String data = (String) response.getBody();
        log.info("oa info response : {}", data);
        JSONObject jsonObject = new JSONObject(data);
        if (jsonObject.has("error")) {
            Integer error = jsonObject.getInt("error");
            if (error == 0) {
                OaInfoResponse oaInfoResponse = JsonUtils.jsonToObject(jsonObject.toString(), OaInfoResponse.class);
                return oaInfoResponse;
            } else {
               JsonUtils.returnErrorResponse(jsonObject.toString());
            }
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    @Override
    @SneakyThrows
    public UserOaInfoResponse getUserOfOa(ShopZaloConfigEntity config, Integer offset, Integer count) {
        String data = String.format("{\"offset\":\"%d\",\"count\":\"%d\"}", offset, count);
        String endPoint = String.format(zaloOAProperty.getUserUrl() + "?access_token=%s&data={data}", config.getAccessToken());
        try {
            log.info("Forward request to [{}], method [{}]", endPoint, "GET");
            ResponseEntity response = restTemplate.getForEntity(endPoint, String.class, data);
            String body = (String) response.getBody();
            log.info("list user of oa response : {}", body);
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has("error")) {
                Integer error = jsonObject.getInt("error");
                if (error == 0) {
                    UserOaInfoResponse userOaInfoResponse = objectMapper.readValue(jsonObject.toString(), new TypeReference<UserOaInfoResponse>() {
                    });
                    for (UserOaInfoResponse.OaData.Follower follower : userOaInfoResponse.getData().getFollowers()) {
                        UserProfileResponse profileResponse = getUserProfile(config.getAccessToken(), follower.getUserId());
                        UserZaloInfoEntity entity = new UserZaloInfoEntity();
                        entity.setAppId(config.getAppId());
                        entity.setShopId(config.getShopId());
                        entity.setOaId(config.getOaId());
                        entity.setOaUserId(follower.getUserId());
                        AppUtils.copyPropertiesIgnoreNull(profileResponse.getData(), entity);
                        entity.setCreatedDate(new Date());
                        userZaloInfoDao.save(entity);
                    }
                    return userOaInfoResponse;
                } else {
                    JsonUtils.returnErrorResponse(jsonObject.toString());
                }
            }
        } catch (JsonProcessingException e) {
            log.info("error get list user oa info: {}", e.getMessage());
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }


    @Override
    public ResponseEntity saveUserOa(Long shopId) {
        ShopZaloConfigEntity zaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (zaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        UserOaInfoResponse response = getUserOfOa(zaloConfig, 0, 50);
        Integer total = response.getData().getTotal();
        if (total > 50) {
            Integer number = total / 50;
            int i = 1;
            while (i < number) {
                getUserOfOa(zaloConfig, i * 50, 50);
                i += 1;
            }
        }
        return ResponseFactory.success();
    }

    @Override
    @SneakyThrows
    public UserProfileResponse getUserProfile(String accessToken, String userId) {
        String data = String.format("{\"user_id\" : \"%s\"}", userId);
        String endPoint = String.format(zaloOAProperty.getUserProfileUrl() + "?access_token=%s&data={data}", accessToken);
        log.info("Forward request to [{}], method [{}]", endPoint, "GET");
        try {
            ResponseEntity response = restTemplate.getForEntity(endPoint, String.class, data);
            String body = (String) response.getBody();
            log.info("user {} of oa response : {}", userId, body);
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has("error")) {
                Integer error = jsonObject.getInt("error");
                if (error == 0) {
                    UserProfileResponse userOaInfoResponse = JsonUtils.jsonToObject(jsonObject.toString(), UserProfileResponse.class);
                    return userOaInfoResponse;
                } else {
                    JsonUtils.returnErrorResponse(jsonObject.toString());
                }
            }
        }
        catch (Exception e){
            log.info("error connect to {}", zaloOAProperty.getUserProfileUrl());
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    @Override
    public ResponseEntity getUserOfShop(String keyword, Long shopId, Long begin, Long end, Pageable pageable) {
        Page<UserZaloInfoEntity> userZaloInfos = userZaloInfoDao.findAll(makeQueryUser(keyword, shopId, begin, end), pageable);
        return ResponseFactory.success(userZaloInfos);
    }

    @Override
    public ShopChatResponse getAllChatOfShop(Long shopId) {
        ShopChatResponse shopChatResponse = getChatOfShop(shopId, 0, 10);
        if (shopChatResponse.getData().size() == 10) {
            int i = 1;
            while (true) {
                ShopChatResponse response = getChatOfShop(shopId, i * 10, 10);
                if (response.getData() == null || response.getData().isEmpty())
                    break;
                shopChatResponse.getData().addAll(response.getData());
                i += 1;
            }
        }
        return shopChatResponse;
    }

    @SneakyThrows
    public ShopChatResponse getChatOfShop(Long shopId, Integer offset, Integer count) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (shopZaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        String data = String.format("{\"offset\":\"%d\",\"count\":\"%d\"}", offset, count);
        String endPoint = String.format(zaloOAProperty.getUserListrecentchatUrl() + "?access_token=%s&data={data}", shopZaloConfig.getAccessToken());
        try {
            log.info("Forward request to [{}], method [{}]", endPoint, "GET");
            ResponseEntity response = restTemplate.getForEntity(endPoint, String.class, data);
            String body = (String) response.getBody();
            log.info("response body get chat of shop {}: {}", shopId, body);
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has("error")) {
                Integer error = jsonObject.getInt("error");
                if (error == 0) {
                    ShopChatResponse chatResponse = JsonUtils.jsonToObject(jsonObject.toString(), ShopChatResponse.class);
                    return chatResponse;
                } else {
                   JsonUtils.returnErrorResponse(jsonObject.toString());
                }
            }
        }
        catch (Exception e){
            log.info("error connect to {} : message {}", endPoint, e.getMessage());
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }

    @Override
    public ResponseEntity getAllConversationOfUser(Long shopId, String userId) {
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (shopZaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        ShopChatResponse shopChatResponse = getConversationOfUser(shopZaloConfig.getAccessToken(), userId, 0, 10);
        if (shopChatResponse.getData().size() == 10) {
            int i = 1;
            while (true) {
                ShopChatResponse response = getChatOfShop(shopId, i * 10, 10);
                if (response.getData() == null || response.getData().isEmpty())
                    break;
                shopChatResponse.getData().addAll(response.getData());
                i += 1;
            }
        }
        return ResponseFactory.success(shopChatResponse);
    }

    public ShopChatResponse getConversationOfUser(String accessToken, String userId, Integer offset, Integer count) {
        String data = String.format("{\"user_id\":\"%s\",\"offset\": \"%d\",\"count\":\"%d\"}", userId, offset, count);
        String endPoint = String.format(zaloOAProperty.getUserConversationUrl() + "?access_token=%s&data={data}", accessToken);
        log.info("Forward request to [{}], method [{}]", endPoint, "GET");
        try {

            ResponseEntity response = restTemplate.getForEntity(endPoint, String.class, data);
            String body = (String) response.getBody();
            log.info("response body get conversation  of userId {} : {}", userId, body);
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has("error")) {
                Integer error = jsonObject.getInt("error");
                if (error == 0) {
                    ShopChatResponse chatResponse = JsonUtils.jsonToObject(jsonObject.toString(), ShopChatResponse.class);
                    return chatResponse;
                } else {
                    JsonUtils.returnErrorResponse(jsonObject.toString());
                }
            }
        }
        catch (Exception e){
            log.info("error connect {} message {}", endPoint, e.getMessage());
        }
        throw new CoreException(CoreErrorCode.GENERAL_ERROR);
    }


    Specification<UserZaloInfoEntity> makeQueryUser(String keyword, Long shopId, Long begin, Long end) {

        Specification<UserZaloInfoEntity> specification = Specification.where(null);
        String kw = "%" + keyword + "%";
        specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get(UserZaloInfoEntity.UserZaloInfo_.DISPLAY_NAME), kw),
                criteriaBuilder.like(root.get(UserZaloInfoEntity.UserZaloInfo_.USER_PHONE), kw),
                criteriaBuilder.like(root.get(UserZaloInfoEntity.UserZaloInfo_.OA_USER_ID), kw))));
        if (shopId != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(UserZaloInfoEntity.UserZaloInfo_.SHOP_ID), shopId)));
        if (begin != null && end != null)
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(UserZaloInfoEntity.UserZaloInfo_.CREATE_DATE), new Date(begin), new Date(end))));
        return specification;
    }

    @SneakyThrows
    @Override
    public ResponseEntity sendMessageToUser(String message, String userId, Long shopId) {
        SendMessageRequest.Recipient recipient = new SendMessageRequest.Recipient(userId);
        SendMessageRequest.Message message1 = new SendMessageRequest.Message(message);
        SendMessageRequest request = SendMessageRequest.builder().recipient(recipient).message(message1).build();
        log.info("send message with body {}", objectMapper.writeValueAsString(request));
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if (shopZaloConfig == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        HttpHeaders headers = forwardService.buildHeaders(shopZaloConfig.getAccessToken(), MediaType.APPLICATION_JSON);
        ResponseEntity response = forwardService.forward(zaloOAProperty.getSendMessageCs(), "", HttpMethod.POST, request, headers);
        String body = (String) response.getBody();
        JSONObject jsonObject = new JSONObject(body);
        log.info("response message \"Tư vấn\" from shopId {} : {}", shopId, body);
        if (jsonObject.has("error")) {
            Integer error = jsonObject.getInt("error");
            if (error == 0) {
                SendMessageResponse sendMessageResponse = JsonUtils.jsonToObject(jsonObject.toString(), SendMessageResponse.class);
                return ResponseFactory.success(sendMessageResponse);
            } else {
                JsonUtils.returnErrorResponse(jsonObject.toString());
            }
        }
        throw new CoreException(CoreErrorCode.BAD_REQUEST);
    }
}
