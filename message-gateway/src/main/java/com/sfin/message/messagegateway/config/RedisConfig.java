package com.sfin.message.messagegateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    JedisConnectionFactory jedisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration= new RedisStandaloneConfiguration(redisHost, redisPort);
        if(redisPassword != null && redisPassword.length()>0)
            redisStandaloneConfiguration.setPassword(redisPassword);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    @Primary
    RedisTemplate<Object, Object> redisTemplate(){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}
