package com.sfin.message.messagegateway.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class CoreException extends RuntimeException{

    private String code;
    private String message;
    private String label;
    private HttpStatus status;
    private Object data;

    public CoreException(){
        super();
    }

    public CoreException(CoreErrorCode code, Map<?, ?> data){
        super();
        this.code = code.code();
        this.message = code.message();
        this.status = code.status();
        this.data = data;
        this.label = code.label();
    }

    public CoreException(CoreErrorCode code, String message){
        super();
        this.code = code.code();
        this.message = message;
        this.status = code.status();
        this.label = code.label();
    }

    public CoreException(CoreErrorCode code, String... errors) {
        super();
        this.code = code.code();
        this.message = code.message();
        this.status = code.status();
        this.label = code.label();

        if (errors != null) {
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < errors.length / 2; i++) {
                map.put(errors[i * 2], errors[i * 2 + 1]);
            }
            this.data = map;
        }
    }
}
