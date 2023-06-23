package com.sfin.message.messagegateway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import lombok.Data;

@Data
public class SendMessageResponse extends ErrorResponse {

    DataResponse data;

    @Data
    public static class DataResponse {

        @JsonProperty(value = "message_id")
        String messageId;
        @JsonProperty(value = "user_id")
        String userId;
    }
}
