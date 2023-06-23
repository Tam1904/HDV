package com.sfin.message.messagegateway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopChatResponse extends ErrorResponse {

    List<ChatData> data;
    @Data
    public static class ChatData implements Serializable {

        @JsonProperty(value = "message_id")
        private String messageId;
        Integer src;
        Long time;
        String type;
        String message;
        String thumb;
        String url;
        @JsonProperty(value = "from_id")
        Long fromId;
        @JsonProperty(value = "to_id")
        Long toId;
        @JsonProperty(value = "from_display_name")
        String fromDisplayName;
        @JsonProperty(value = "from_avatar")
        String fromAvatar;
        @JsonProperty(value = "to_display_name")
        String toDisplayName;
        @JsonProperty(value = "to_avatar")
        String avatar;
        String location;
    }
}
