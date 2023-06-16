package com.sfin.message.messagegateway.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.UnknownProfileException;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendData {
    @JsonProperty(value = "sent_time")
    Date sendTime;
    Quota quota;
    @JsonProperty(value = "msg_id")
    String msgId;
}
