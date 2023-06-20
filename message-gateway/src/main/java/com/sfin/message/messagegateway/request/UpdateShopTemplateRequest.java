package com.sfin.message.messagegateway.request;

import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;

public class UpdateShopTemplateRequest {

    Boolean active;
    ShopTemplatesEntity.Type type;
}
