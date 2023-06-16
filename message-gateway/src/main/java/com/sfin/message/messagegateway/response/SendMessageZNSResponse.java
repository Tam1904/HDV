package com.sfin.message.messagegateway.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SendMessageZNSResponse {

    Integer error;
    String message;
    SendData data;
}

