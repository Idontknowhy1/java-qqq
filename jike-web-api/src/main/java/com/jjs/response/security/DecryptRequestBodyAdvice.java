package com.jjs.response.security;


import com.jjs.common.SecurityUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@ControllerAdvice
@ConditionalOnProperty(name = "secret.enabled", havingValue = "true")
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Autowired
    SecretConfig secretConfig;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
//        if (methodParameter.getMethod().isAnnotationPresent(ResponseNoSecurity.class)
//                || methodParameter.getMethod().getDeclaringClass().isAnnotationPresent(ResponseNoSecurity.class)){
//            return false;
//        }
//        return true;
        return  false;
    }

    @NotNull
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String cryptString = inToString(inputMessage.getBody());
        String body = cryptString;
        // 加密
        body = SecurityUtil.aes.decrypt(cryptString, secretConfig.getKey());
        final String _body = body;
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(_body.getBytes()) ;
            }
        } ;
    }

    private String inToString(InputStream is) {
        byte[] buf = new byte[10 * 1024] ;
        int leng = -1 ;
        StringBuilder sb = new StringBuilder() ;
        try {
            while ((leng = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, leng)) ;
            }
            return sb.toString() ;
        } catch (IOException e) {
            throw new RuntimeException(e) ;
        }
    }

}
