package com.sfin.message.messagegateway.request;

import lombok.Data;

import java.util.List;

@Data
public class TemplateShopRequest {

    List<ShopTemplateRequest> shopTemplate;

}
