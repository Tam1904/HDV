package com.sfin.message.messagegateway.response;

import lombok.Data;

@Data
public class DataTemplate {
    Integer templateId;
    String templateName;
    Long createdTime;
    String status;
    String templateQuality;
    TemplateDetailResponse detailResponse;
}
