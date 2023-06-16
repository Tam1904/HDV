package com.sfin.message.messagegateway.response;

import lombok.*;


@Data
public class TemplateDetailResponse {

    private Integer error;
    private String message;
    private TemplateData data;
}
