package com.jike.interceptor;

import com.jike.request.AppRequestHeader;
import com.jike.utils.JwtTokenUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class AppRequestHeaderResolver implements HandlerMethodArgumentResolver {

    /**
     * 判断该解析器是否支持处理 AppRequestHeader 类型的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AppRequestHeader.class);
    }

    /**
     * 实际解析参数的方法
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        AppRequestHeader headers = new AppRequestHeader();

        // 手动从请求头中取值并设置到对象中
        String token = request.getHeader("x-token");
        if (token != null) {
            headers.setToken(token);
        }
        if (token != null && !token.isEmpty()) {
            String userId = JwtTokenUtil.parseToken(token);
            if (userId != null) {
                headers.setUserId(userId);
            }
        }

        return headers;
    }
}