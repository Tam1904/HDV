package com.sfin.message.messagegateway.response;

import lombok.Data;

import java.util.List;

@Data
public class TemplateData {
    Integer templateId;
    String templateName;
    String status;
    List<TemplateParams> listParams;
    Long timeout;
    String previewUrl;
    String templateQuality;
    String templateTag;
    String price;
    Boolean applyTemplateQuota;
}