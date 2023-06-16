package com.sfin.message.messagegateway.interceptor;

import com.sfin.eplaform.commons.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Log4j2
public class GatewayInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correlationId = request.getHeader(Constants.HEADER.REQUEST_ID_KEY);
        if(correlationId == null)
            correlationId = RandomStringUtils.randomAlphabetic(6);
        String ipClient = request.getRemoteAddr();
        ThreadContext.put(Constants.HEADER.REQUEST_ID_KEY, correlationId);
        log.info("========== Start process request [{}]:[{}] from IP [{}]", request.getMethod(), request.getServletPath(), ipClient);
        request.setAttribute(Constants.HEADER.REQUEST_ID_KEY, System.currentTimeMillis());
        return verifyRequest(request);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long startTime = (Long) request.getAttribute(Constants.HEADER.REQUEST_ID_KEY);
        Long processTime = System.currentTimeMillis() - startTime;
        log.info("========= End process request [{}]:[{}] with [{}]. Processing time [{}]", request.getMethod(), request.getServletPath(), response.getStatus(), processTime);
    }

    private boolean verifyRequest(HttpServletRequest request){
        String userId = request.getHeader(Constants.HEADER.USER_ID);
        if(userId == null)
            userId = request.getHeader(Constants.HEADER.CUSTOMER_ID);
        String phone  = request.getHeader(Constants.HEADER.CUSTOMER_PHONE);
        String token = request.getHeader(Constants.HEADER.CUSTOMER_TOKEN);
         Payload payload = new Payload();
         if(!StringUtils.isBlank(userId)){
             payload.setCustomerId(Long.valueOf(userId));
             payload.setPhone(phone);
             payload.setToken(token);
             request.setAttribute(Constants.PAYLOAD, payload);
             log.info("Request validated. Start forward request to backend");
         }
         return true;
    }
}
