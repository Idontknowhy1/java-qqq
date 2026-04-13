package com.jjs.response.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjs.common.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@ConditionalOnProperty(name = "secret.enabled", havingValue = "true")
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    SecretConfig secretConfig;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.getMethod().isAnnotationPresent(ResponseNoSecurity.class)
                || returnType.getMethod().getDeclaringClass().isAnnotationPresent(ResponseNoSecurity.class)){
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body == null) {
            return body ;
        }
        try {
            String jsonStr = new ObjectMapper().writeValueAsString(body) ;
            String result = jsonStr;
            result = SecurityUtil.aes.encrypt(jsonStr, secretConfig.getKey());
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }

}
