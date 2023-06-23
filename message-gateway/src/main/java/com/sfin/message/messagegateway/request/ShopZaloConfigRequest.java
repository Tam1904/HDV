package com.sfin.message.messagegateway.request;

import lombok.Data;

import java.util.Date;

@Data
public class ShopZaloConfigRequest extends UpdateShopZaloConfigRequest{

    private Long shopId;
}
