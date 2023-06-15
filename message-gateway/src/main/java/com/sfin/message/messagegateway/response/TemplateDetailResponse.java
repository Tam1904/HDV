package com.sfin.message.messagegateway.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDetailResponse {

    private Integer error;
    private String message;
    private TemplateData data;
}

class TemplateData {
    Integer templateId;
    String templateName;
    String status;
    List<TemplateParams> listParams;
    Long timeOut;
    String previewUrl;
    String templateQuality;
    String templateTag;
    String price;
    boolean applyTemplateQuota;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TemplateParams> getListParams() {
        return listParams;
    }

    public void setListParams(List<TemplateParams> listParams) {
        this.listParams = listParams;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getTemplateQuality() {
        return templateQuality;
    }

    public void setTemplateQuality(String templateQuality) {
        this.templateQuality = templateQuality;
    }

    public String getTemplateTag() {
        return templateTag;
    }

    public void setTemplateTag(String templateTag) {
        this.templateTag = templateTag;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isApplyTemplateQuota() {
        return applyTemplateQuota;
    }

    public void setApplyTemplateQuota(boolean applyTemplateQuota) {
        this.applyTemplateQuota = applyTemplateQuota;
    }
}

class TemplateParams{
    String name;
    boolean require;
    String type;
    Integer maxLength;
    Integer minLength;
    boolean acceptNull;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public boolean isAcceptNull() {
        return acceptNull;
    }

    public void setAcceptNull(boolean acceptNull) {
        this.acceptNull = acceptNull;
    }
}
