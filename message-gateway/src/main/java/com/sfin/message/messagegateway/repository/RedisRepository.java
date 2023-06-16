package com.sfin.message.messagegateway.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfin.message.messagegateway.request.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RedisRepository {

    private final String ZNS_KEY= "zns_key";

    private final RedisTemplate<String, NotificationRequest> redisTemplate;

    public RedisRepository(RedisTemplate<String, NotificationRequest> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public synchronized void addMessageToQueue(NotificationRequest request){
        redisTemplate.opsForList().rightPush(ZNS_KEY, request);
    }

    public synchronized List<NotificationRequest> messageZnsList(){
        List<NotificationRequest> requests = new ArrayList<>();
        while (true){
            NotificationRequest request = redisTemplate.opsForList().leftPop(ZNS_KEY);
            if(request == null)
                break;
            requests.add(request);
        }
        return requests;
    }
}
