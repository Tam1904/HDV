package com.sfin.message.messagegateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zalo-oa")
@Data
public class ZaloOAProperty {

    private String accessTokenUrl;
    private String authorizationCodeUrl;
    private String znsUrl;
    private String redirectUrl;
    private String templateUrl;
    private String templateInfoUrl;
}
