package com.sfin.message.messagegateway.service;

import com.sfin.message.messagegateway.request.ShopTemplateRequest;
import com.sfin.message.messagegateway.request.TemplateShopRequest;
import com.sfin.message.messagegateway.request.UpdateShopTemplateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ShopZaloService {

    ResponseEntity getShopTemplateRegister(Long shopId, Integer status, Integer limit);

    ResponseEntity createShopTemplate(Long shopId, TemplateShopRequest request);


    ResponseEntity getTemplateOfShop(Long shopId, String keyword, Long begin, Long end, Boolean active, Pageable pageable);

    ResponseEntity getTemplateDetailOfShop(Long shopTemplateId);

    ResponseEntity updateTemplateShop(Long shopTemplateId, UpdateShopTemplateRequest request);

    ResponseEntity deleteShopTemplate(Long shopTemplateId);

    ResponseEntity getHistorySendMessageZns(String keyword, Long begin, Long end, Long shopId, Integer templateId, Integer error, Pageable pageable);
}
