package com.sfin.message.messagegateway.service.impl;

import com.sfin.eplaform.commons.utils.Definition;
import com.sfin.message.messagegateway.service.ForwardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
            ResponseEntity response = restTemplate.exchange(endPoint, method, entity, String.class);
            return response;
        } catch (Exception ex){
            log.info("Can not connect to [{}]", endPoint);
            throw ex;
        }
    }

    @Override
    public HttpHeaders buildHeaders(String accessToken, MediaType mediaType){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.add(Definition.ZALO_CONFIG.ACCESS_TOKEN, accessToken);
        return headers;
    }

}
