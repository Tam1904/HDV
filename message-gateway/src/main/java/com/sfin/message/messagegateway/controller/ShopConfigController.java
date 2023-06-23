package com.sfin.message.messagegateway.controller;

import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.message.messagegateway.interceptor.Payload;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.AuthorizationCodeRequest;
import com.sfin.message.messagegateway.request.NotificationRequest;
import com.sfin.message.messagegateway.request.ShopZaloConfigRequest;
import com.sfin.message.messagegateway.request.UpdateShopZaloConfigRequest;
import com.sfin.message.messagegateway.service.ZaloService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop-config")
@Log4j2
public class ShopConfigController {

    @Autowired
    public ZaloService zaloService;


    @PostMapping
    public ResponseEntity createZaloOAConfig(@RequestAttribute Payload payload
            , @RequestParam Long shopId
            , @RequestBody UpdateShopZaloConfigRequest request) {
        log.info("create shop zalo oa config {}", request);
        ShopZaloConfigRequest configRequest = new ShopZaloConfigRequest();
        AppUtils.copyPropertiesIgnoreNull(request, configRequest);
        configRequest.setShopId(shopId);
        zaloService.createZaloOAConfig(configRequest);

        log.info("generate auth code for shopId {}", shopId);
        String endPoint = zaloService.generateUrlCode(shopId, request.getOaId());
        return ResponseFactory.success(endPoint);
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity updateZaloOAConfig(@RequestAttribute Payload payload
            , @PathVariable Long id
            , @RequestParam Long shopId
            , @RequestBody UpdateShopZaloConfigRequest request) {
        log.info("update shop zalo oa config {}", request);
        ShopZaloConfigEntity entity = zaloService.updateZaloOAConfig(id, request);
        return ResponseFactory.success(entity);
    }

    @GetMapping("/generate-auth-code")
    public ResponseEntity generateUrlCode(@RequestParam Long shopId, @RequestParam String oaId) {
        log.info("generate auth code for shopId {}", shopId);
        String endPoint = zaloService.generateUrlCode(shopId, oaId);
        return ResponseFactory.success(endPoint);
    }

    @PostMapping("/get-access-token")
    public ResponseEntity updateAuthorizationCode(@RequestBody AuthorizationCodeRequest request) {
        log.info("save authorization code {}", request);
        ShopZaloConfigEntity shopZaloConfig = zaloService.updateAuthorizationCode(request);
        return ResponseFactory.success(shopZaloConfig);
    }


}
