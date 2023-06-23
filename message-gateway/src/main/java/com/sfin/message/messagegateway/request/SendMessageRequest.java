package com.sfin.message.messagegateway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {

    Recipient recipient;
    Message message;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Recipient {
        @JsonProperty(value = "user_id")
        String userId ;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        String text;
    }
}
