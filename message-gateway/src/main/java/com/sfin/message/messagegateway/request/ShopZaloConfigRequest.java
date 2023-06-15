package com.sfin.message.messagegateway.request;

import lombok.Data;

import java.util.Date;

@Data
public class ShopZaloConfigRequest {

    private Long shopId;
    private String appId;
    private String secretKey;
    private String oaId;

}
