package com.sfin.message.messagegateway.controller;

import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.message.messagegateway.repository.entity.HistorySendMessage;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.AuthorizationCodeRequest;
import com.sfin.message.messagegateway.request.NotificationRequest;
import com.sfin.message.messagegateway.request.ShopZaloConfigRequest;
import com.sfin.message.messagegateway.service.ZaloService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
@Log4j2
public class NotificationController {

    @Autowired
    public ZaloService zaloService;


    @PostMapping("/shop-config")
    public ResponseEntity createZaloOAConfig(@RequestBody ShopZaloConfigRequest request) {
        log.info("create shop zalo oa config {}", request);
        ShopZaloConfigEntity entity = zaloService.createZaloOAConfig(request);
        return ResponseFactory.success(entity);
    }

    @GetMapping("/generate-auth-code")
    public ResponseEntity generateUrlCode(@RequestParam Long shopId) {
        log.info("generate auth code for shopId {}", shopId);
        String endPoint = zaloService.generateUrlCode(shopId);
        return ResponseFactory.success(endPoint);
    }

    @PostMapping("/update-auth-code")
    public ResponseEntity updateAuthorizationCode(@RequestBody AuthorizationCodeRequest request) {
        log.info("save authorization code {}", request);
        ShopZaloConfigEntity shopZaloConfig = zaloService.updateAuthorizationCode(request);
        return ResponseFactory.success(shopZaloConfig);
    }

    @PostMapping("/send-zalo-message")
    public ResponseEntity sendMessage(@RequestBody NotificationRequest request) {
        zaloService.addMessageToRedis(request);
        return ResponseFactory.success();
    }


}
