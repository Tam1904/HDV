package com.sfin.message.messagegateway.request;

import lombok.Data;

@Data
public class AuthorizationCodeRequest {

    private String oaId;
    private String code;
    private String codeChallenge;
}
