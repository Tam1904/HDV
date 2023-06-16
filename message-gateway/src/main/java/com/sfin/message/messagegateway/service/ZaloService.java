package com.sfin.message.messagegateway.service;

import com.sfin.message.messagegateway.repository.entity.HistorySendMessage;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.AuthorizationCodeRequest;
import com.sfin.message.messagegateway.request.NotificationRequest;
import com.sfin.message.messagegateway.request.ShopZaloConfigRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

public interface ZaloService {
    ShopZaloConfigEntity createZaloOAConfig(ShopZaloConfigRequest request);

    String generateUrlCode(Long shopId);

    ShopZaloConfigEntity updateAuthorizationCode(AuthorizationCodeRequest request);

    ShopZaloConfigEntity getAccessToken(ShopZaloConfigEntity zaloConfig);

    ShopZaloConfigEntity updateAccessToken(ShopZaloConfigEntity zaloConfig);

    @Async
    void sendMessage(NotificationRequest request);

    void addMessageToRedis(NotificationRequest request);
}
