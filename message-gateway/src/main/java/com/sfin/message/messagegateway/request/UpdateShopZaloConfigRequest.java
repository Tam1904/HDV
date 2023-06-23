package com.sfin.message.messagegateway.request;

import lombok.Data;

@Data
public class UpdateShopZaloConfigRequest {

    private String appId;
    private String secretKey;
    private String oaId;
}
