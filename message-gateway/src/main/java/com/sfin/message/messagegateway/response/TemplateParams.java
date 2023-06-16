package com.sfin.message.messagegateway.response;

import lombok.Data;

@Data
public class TemplateParams{
    String name;
    boolean require;
    String type;
    Integer maxLength;
    Integer minLength;
    boolean acceptNull;
}
