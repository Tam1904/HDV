package com.sfin.message.messagegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class MessageGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageGatewayApplication.class, args);
    }

}
