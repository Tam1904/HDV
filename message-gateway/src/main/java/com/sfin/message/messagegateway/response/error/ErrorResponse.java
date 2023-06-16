package com.sfin.message.messagegateway.response.error;

import lombok.Data;

@Data
public class ErrorResponse {

    private Integer error;
    private String message;
}
