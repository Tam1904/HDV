package com.sfin.message.messagegateway.service.impl;

import com.sfin.message.messagegateway.service.ForwardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class ForwardServiceImpl implements ForwardService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity forward(String baseUrl, String path, HttpMethod method, Object requestBody, HttpHeaders header){
        String endPoint = baseUrl + path;
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, header);
        log.info("Forward request to [{}], method [{}]", endPoint, method.name());
        try {
            ResponseEntity response = restTemplate.exchange(endPoint, method, entity, Object.class);
            return response;
        } catch (Exception ex){
            log.info("Can not connect to [{}]", endPoint);
            throw ex;
        }
    }

    @Override
    public <T> T getValue(Object data, Class<T> clazz){
        try {
            return clazz.cast(data);
        }
        catch (ClassCastException exception){
            throw exception;
        }
    }
}
