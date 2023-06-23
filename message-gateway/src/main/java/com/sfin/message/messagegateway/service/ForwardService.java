package com.sfin.message.messagegateway.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface ForwardService {
    ResponseEntity forward(String baseUrl, String path, HttpMethod method, Object requestBody, HttpHeaders header);

    HttpHeaders buildHeaders(String accessToken, MediaType mediaType);
}
