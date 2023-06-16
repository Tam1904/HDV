package com.sfin.message.messagegateway.response;

import lombok.Data;

@Data
public class Quota {
    String remainingQuota;
    String dailyQuota;
}
