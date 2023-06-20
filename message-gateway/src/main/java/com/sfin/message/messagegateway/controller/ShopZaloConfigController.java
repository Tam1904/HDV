package com.sfin.message.messagegateway.controller;

import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.interceptor.Payload;
import com.sfin.message.messagegateway.request.ShopTemplateRequest;
import com.sfin.message.messagegateway.request.TemplateShopRequest;
import com.sfin.message.messagegateway.request.UpdateShopTemplateRequest;
import com.sfin.message.messagegateway.service.ShopZaloService;
import com.sfin.message.messagegateway.utils.DaoUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop-zalo")
@Log4j2
public class ShopZaloConfigController {

    @Autowired
    private ShopZaloService shopZaloService;

    @GetMapping("/template-register")
    public ResponseEntity getShopTemplateRegister(@RequestAttribute Payload payload
            , @RequestParam(value = "shopId") Long shopId
            , @RequestParam(value = "status", required = false) Integer status
            , @RequestParam(value = "limit", defaultValue = "100") Integer limit){
        if(payload == null)
            throw new CoreException(CoreErrorCode.UNAUTHORIZED);
        log.info("get template shop {} register  zalo template ");
        return shopZaloService.getShopTemplateRegister(shopId, status, limit);
    }

    @PostMapping
    public ResponseEntity createShopTemplate(@RequestAttribute Payload payload
            , @RequestParam long shopId
            , @RequestBody TemplateShopRequest request){
        if(payload == null)
            throw new CoreException(CoreErrorCode.UNAUTHORIZED);
        log.info("create shop template with shopId {} ", shopId);
        return shopZaloService.createShopTemplate(shopId, request);
    }

    @GetMapping
    public ResponseEntity getTemplateOfShop(@RequestAttribute Payload payload
            , @RequestParam Long shopId
            , @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword
            , @RequestParam(value = "beginDate", required = false) Long begin
            , @RequestParam(value = "endDate", required = false) Long end
            , @RequestParam(value = "active", required = false) Boolean active
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page
            , @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize
            , @RequestParam(value = "orderBy", defaultValue = "createdDate", required = false) String orderBy
            , @RequestParam(value = "direction", defaultValue = "DESC", required = false) String direction){
        if(payload == null)
            throw new CoreException(CoreErrorCode.UNAUTHORIZED);
        Pageable pageable = DaoUtils.buildPageable(page, pageSize, orderBy, direction);
        log.info("template of shop {}", shopId);
        return shopZaloService.getTemplateOfShop(shopId, keyword, begin, end, active, pageable);
    }

    @GetMapping("/{shopTemplateId:\\d+}")
    public ResponseEntity getTemplateDetail(@RequestAttribute Payload payload
            , @RequestParam(value = "shopId") Long shopId
            , @PathVariable Long shopTemplateId){
        if(payload == null)
            throw new CoreException(CoreErrorCode.UNAUTHORIZED);
        log.info("template {} of shop {}", shopTemplateId,  shopId);
        return shopZaloService.getTemplateDetailOfShop(shopTemplateId);
    }

    @PutMapping("/{shopTemplateId:\\d+}")
    public ResponseEntity updateTemplateShop(@RequestAttribute Payload payload
            , @PathVariable Long shopTemplateId
            , @RequestParam(value = "shopId") Long shopId
            , @RequestBody UpdateShopTemplateRequest request){
        if(payload == null)
            throw new CoreException(CoreErrorCode.UNAUTHORIZED);
        log.info("update template {} of shop request {}", shopTemplateId, request);
        return shopZaloService.updateTemplateShop(shopTemplateId, request);
    }

    @DeleteMapping("/{shopTemplateId:\\d+}")
    public ResponseEntity deleteShopTemplate(@RequestAttribute Payload payload
            , @PathVariable Long shopTemplateId
            , @RequestParam(value = "shopId") Long shopId){
        if(payload == null)
            throw new CoreException(CoreErrorCode.UNAUTHORIZED);
        log.info("delete template {} ", shopTemplateId);
        return shopZaloService.deleteShopTemplate(shopTemplateId);
    }

    @GetMapping("history-zns-message")
    public ResponseEntity getHistorySendMessageZns(@RequestAttribute Payload payload
            , @RequestParam Long shopId
            , @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword
            , @RequestParam(value = "beginDate", required = false) Long begin
            , @RequestParam(value = "endDate", required = false) Long end
            , @RequestParam(value = "active", required = false) Boolean active
            , @RequestParam(value = "templateId", required = false) Integer templateId
            , @RequestParam(value = "error", required = false) Integer error
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page
            , @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize
            , @RequestParam(value = "orderBy", defaultValue = "createdDate", required = false) String orderBy
            , @RequestParam(value = "direction", defaultValue = "DESC", required = false) String direction){
        if(payload == null)
            throw new CoreException(CoreErrorCode.UNAUTHORIZED);
        Pageable pageable = DaoUtils.buildPageable(page, pageSize, orderBy, direction);
        log.info("view history zns message of shop {}", shopId);
        return shopZaloService.getHistorySendMessageZns(keyword, begin, end, shopId, templateId, error, pageable);
    }

}
