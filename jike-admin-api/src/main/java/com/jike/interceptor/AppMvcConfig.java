package com.jike.interceptor;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AppMvcConfig implements WebMvcConfigurer {

    @Autowired
    AppRequestHeaderResolver appRequestHeaderResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截运营模块所有接口请求
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/**");
        // SA 权限验证
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");

//        SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//        SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));

//                .excludePathPatterns("/login/**");
    }

    @Bean
    public LoginInterceptor authenticationInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(appRequestHeaderResolver);
    }
}
