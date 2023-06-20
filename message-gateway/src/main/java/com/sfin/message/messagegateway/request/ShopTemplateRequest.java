package com.sfin.message.messagegateway.request;

import com.sfin.message.messagegateway.repository.entity.ShopTemplatesEntity;
import lombok.Data;

@Data
public class ShopTemplateRequest {

    private Integer templateId;
    private ShopTemplatesEntity.Type type;
    private Boolean active;
}
