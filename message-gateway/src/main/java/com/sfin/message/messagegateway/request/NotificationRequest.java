package com.sfin.message.messagegateway.request;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class NotificationRequest implements Serializable {

   Map<String, Object> data;
}
