package com.sfin.message.messagegateway.interceptor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Payload {

    private Long customerId;
    private String phone;
    private String token;
}
