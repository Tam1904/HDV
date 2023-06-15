package com.sfin.message.messagegateway.response.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenErrorResponse {

    @JsonProperty(value = "error_name")
    private String errorName;

    @JsonProperty(value = "error_reason")
    private String errorReason;

    @JsonProperty(value = "ref_doc")
    private String refDoc;

    @JsonProperty(value = "error_description")
    private String errorDescription;

    @JsonProperty(value = "error")
    private String error;
}
