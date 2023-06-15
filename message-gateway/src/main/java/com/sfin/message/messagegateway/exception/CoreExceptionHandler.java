package com.sfin.message.messagegateway.exception;

import com.sfin.eplaform.commons.exception.ErrorResponse;
import com.sfin.eplaform.commons.exception.RequestLog;
import com.sfin.eplaform.commons.response.ResponseStatus;
import com.sfin.eplaform.commons.utils.Definition;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.BindException;

@ControllerAdvice
public class CoreExceptionHandler {

    @ExceptionHandler(value = CoreException.class)
    public Object handleAppException(HttpServletRequest request, CoreException coreException) throws IOException{
        ErrorResponse<Object> errorResponse = new ErrorResponse<>();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(coreException.getCode());
        responseStatus.setMessage(coreException.getMessage());
        responseStatus.setLabel(coreException.getLabel());
        errorResponse.setStatus(responseStatus);
        errorResponse.setData(coreException.getData());
        RequestLog.error(request, coreException.getStatus().value(), errorResponse, coreException);
        return new ResponseEntity<>(errorResponse, coreException.getStatus());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Object handleMissingParamException(HttpServletRequest request, MissingServletRequestParameterException re)
            throws IOException {
        ErrorResponse<Object> errorResponse = new ErrorResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(CoreErrorCode.MISS_PARAM.code());
        responseStatus.setLabel(CoreErrorCode.MISS_PARAM.label());
        responseStatus.setMessage("Miss params"+ getId());
        errorResponse.setStatus(responseStatus);
        RequestLog.error(request, CoreErrorCode.MISS_PARAM.status().value(), errorResponse, re);
        return new ResponseEntity<>(errorResponse, CoreErrorCode.MISS_PARAM.status());
    }

    @ExceptionHandler(value = BindException.class)
    public Object handleBindException(HttpServletRequest request, BindException re) throws IOException {
        ErrorResponse<Object> errorResponse = new ErrorResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(CoreErrorCode.BAD_REQUEST.code());
        responseStatus.setLabel(CoreErrorCode.BAD_REQUEST.label());
        responseStatus.setMessage(getId() + "Address already in use");
        errorResponse.setStatus(responseStatus);
        RequestLog.error(request, CoreErrorCode.BAD_REQUEST.status().value(), errorResponse, re);
        return new ResponseEntity<>(errorResponse, CoreErrorCode.BAD_REQUEST.status());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpServletRequest request, BindException re) throws IOException {
        ErrorResponse<Object> errorResponse = new ErrorResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(CoreErrorCode.BAD_REQUEST.code());
        responseStatus.setLabel(CoreErrorCode.BAD_REQUEST.label());
        responseStatus.setMessage(getId() + "Http Message Not Readable");
        errorResponse.setStatus(responseStatus);
        RequestLog.error(request, CoreErrorCode.BAD_REQUEST.status().value(), errorResponse, re);
        return new ResponseEntity<>(errorResponse, CoreErrorCode.BAD_REQUEST.status());
    }

    @ExceptionHandler(value = MissingServletRequestPartException.class)
    public Object handleMissingServletRequestPartException(HttpServletRequest request, BindException re) throws IOException {
        ErrorResponse<Object> errorResponse = new ErrorResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(CoreErrorCode.BAD_REQUEST.code());
        responseStatus.setLabel(CoreErrorCode.BAD_REQUEST.label());
        responseStatus.setMessage("Miss path" + getId());
        errorResponse.setStatus(responseStatus);
        RequestLog.error(request, CoreErrorCode.BAD_REQUEST.status().value(), errorResponse, re);
        return new ResponseEntity<>(errorResponse, CoreErrorCode.BAD_REQUEST.status());
    }

    @ExceptionHandler(value = Exception.class)
    public Object handleException(HttpServletRequest request, BindException re) throws IOException {
        ErrorResponse<Object> errorResponse = new ErrorResponse();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode(CoreErrorCode.BAD_REQUEST.code());
        responseStatus.setLabel(CoreErrorCode.BAD_REQUEST.label());
        responseStatus.setMessage("Có lỗi xảy ra, vui lòng đợi trong ít phút" + getId());
        errorResponse.setStatus(responseStatus);
        RequestLog.error(request, CoreErrorCode.BAD_REQUEST.status().value(), errorResponse, re);
        return new ResponseEntity<>(errorResponse, CoreErrorCode.BAD_REQUEST.status());
    }

    private String getId(){
        return  " (" + ThreadContext.get(Definition.REQUEST_ID_KEY) + ")";
    }
}
