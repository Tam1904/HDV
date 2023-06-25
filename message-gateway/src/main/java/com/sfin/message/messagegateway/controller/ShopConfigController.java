package com.sfin.message.messagegateway.controller;

import com.sfin.eplaform.commons.response.ResponseFactory;
import com.sfin.eplaform.commons.utils.AppUtils;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.interceptor.Payload;
import com.sfin.message.messagegateway.repository.ShopZaloConfigDao;
import com.sfin.message.messagegateway.repository.entity.ShopZaloConfigEntity;
import com.sfin.message.messagegateway.request.AuthorizationCodeRequest;
import com.sfin.message.messagegateway.request.ShopZaloConfigRequest;
import com.sfin.message.messagegateway.request.UpdateShopZaloConfigRequest;
import com.sfin.message.messagegateway.service.ZaloService;
import com.sfin.message.messagegateway.utils.DaoUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop-config")
@Log4j2
public class ShopConfigController {

    @Autowired
    public ZaloService zaloService;
    @Autowired
    public ShopZaloConfigDao shopZaloConfigDao;


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

    @GetMapping()
    public ResponseEntity getAllShopConfig(@RequestParam Long shopId){
        log.info("get shop config {}", shopId );
        return zaloService.getOneShopConfig(shopId);
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

    @GetMapping("/get-access-token")
    public ResponseEntity getAccessTokenOfShop(@RequestParam Long shopId){
        log.info("update access token of shop {}", shopId);
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if(shopId == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        return ResponseFactory.success(zaloService.updateAccessToken(shopZaloConfig));
    }

    @PutMapping("update-access-token")
    public ResponseEntity updateAccessTokenOfShop(@RequestParam Long shopId){
        log.info("update access token of shop {}", shopId);
        ShopZaloConfigEntity shopZaloConfig = shopZaloConfigDao.findOneByShopId(shopId);
        if(shopId == null)
            throw new CoreException(CoreErrorCode.ENTITY_NOT_EXISTS);
        return ResponseFactory.success(zaloService.updateAccessToken(shopZaloConfig));
    }

}
