package com.sfin.message.messagegateway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import lombok.Data;

@Data
public class UserProfileResponse extends ErrorResponse {

    UserData data;

    @Data
    public static class UserData {
        String avatar;
        @JsonProperty(value = "user_gender")
        Integer userGender;
        @JsonProperty(value = "display_name")
        String displayName;
    }
}
