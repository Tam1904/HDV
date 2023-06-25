package com.sfin.message.messagegateway.service;

import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;
import com.sfin.message.messagegateway.request.ShopTemplateRequest;
import com.sfin.message.messagegateway.request.TemplateShopRequest;
import com.sfin.message.messagegateway.request.UpdateShopTemplateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShopZaloService {

    ResponseEntity getShopTemplateRegister(Long shopId, Integer status, Integer limit);

    ResponseEntity createShopTemplate(Long shopId, List<ShopTemplateRequest> requests);


    ResponseEntity getTemplateOfShop(Long shopId, String keyword, Long begin, Long end, Boolean active, ShopTemplatesEntity.Type type, Pageable pageable);

    ResponseEntity getTemplateDetailOfShop(Long shopId, Integer templateId);

//    ResponseEntity updateTemplateShop(Long shopTemplateId, UpdateShopTemplateRequest request);

    ResponseEntity deleteShopTemplate(Long shopTemplateId);

    ResponseEntity getHistorySendMessageZns(String keyword, Long begin, Long end, Long shopId, Integer templateId, Integer error, Pageable pageable);
}
