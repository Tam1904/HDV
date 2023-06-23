package com.sfin.message.messagegateway.request;

import lombok.Data;

@Data
public class MessageRequest {
    String message;
    String userId;
}
