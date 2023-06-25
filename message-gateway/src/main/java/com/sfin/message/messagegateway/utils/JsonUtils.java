package com.sfin.message.messagegateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfin.message.messagegateway.exception.CoreErrorCode;
import com.sfin.message.messagegateway.exception.CoreException;
import com.sfin.message.messagegateway.response.error.ErrorResponse;
import org.apache.tomcat.jni.Error;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <R> R jsonToObject(String jsonString, Class<R> returnClass) {
        try {
            return objectMapper.readValue(jsonString, returnClass);
        } catch (IOException e) {
            throw new CoreException(CoreErrorCode.GENERAL_ERROR, e.getMessage());
        }
    }

    public static void returnErrorResponse(String json) {
        ErrorResponse errorResponse = jsonToObject(json, ErrorResponse.class);
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("error", errorResponse);
        throw new CoreException(CoreErrorCode.BAD_REQUEST, extraData);
    }

}
