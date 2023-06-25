package com.sfin.message.messagegateway.request;

import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;
import lombok.Data;

@Data
public class UpdateShopTemplateRequest {

    Integer templateId;
    Boolean active;
    ShopTemplatesEntity.Type type;
}
