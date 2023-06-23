package com.sfin.message.messagegateway.controller;

import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.message.messagegateway.request.NotificationRequest;
import com.sfin.message.messagegateway.service.ZaloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private ZaloService zaloService;

    @PostMapping("/send-zalo-message")
    public ResponseEntity sendMessage(@RequestBody NotificationRequest request) {
        zaloService.addMessageToRedis(request);
        return ResponseFactory.success();
    }
}
