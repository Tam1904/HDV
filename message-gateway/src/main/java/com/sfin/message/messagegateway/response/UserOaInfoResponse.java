package com.sfin.message.messagegateway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import lombok.Data;

import java.util.List;

@Data
public class UserOaInfoResponse extends ErrorResponse {

    OaData data;

    @Data
    public static class OaData {
        Integer total;
        List<Follower> followers;
        @Data
        public static class Follower {
            @JsonProperty(value = "user_id")
            String userId;
        }
    }
}
