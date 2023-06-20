package com.sfin.message.messagegateway.response;

import lombok.Data;

@Data
public class TemplateParams{
    String name;
    Boolean require;
    String type;
    Integer maxLength;
    Integer minLength;
    Boolean acceptNull;
}
