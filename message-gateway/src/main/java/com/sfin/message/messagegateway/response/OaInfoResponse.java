package com.sfin.message.messagegateway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OaInfoResponse {

    private Integer error;
    private String message;
    private DataOa data;

    @Data
    public class DataOa {

        String description;
        String name;
        String avatar;
        String cover;
        @JsonProperty(value = "oa_id")
        String oaId;
        @JsonProperty(value = "is_verified")
        Boolean verified;
    }

}
