package com.sfin.message.messagegateway.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenRequest {

    @JsonProperty(value = "code")
    String code;
    @JsonProperty(value = "app_id")
    String appId;
    @JsonProperty(value = "grant_type")
    String grantType;
    @JsonProperty(value = "code_verifier")
    String codeVerifier;
}
