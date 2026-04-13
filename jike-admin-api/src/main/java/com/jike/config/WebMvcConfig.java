package com.jike.config;

import com.jike.annotation.AdminApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 为带有@AdminController注解的控制器添加/admin前缀
        configurer.addPathPrefix("/admin",
                c -> c.isAnnotationPresent(AdminApi.class));

//        // 为带有@ApiV1Controller注解的控制器添加/api/v1前缀
//        configurer.addPathPrefix("/v1",
//                c -> c.isAnnotationPresent(ApiV1.class));
//
//        // 为带有@ApiV2Controller注解的控制器添加/api/v2前缀
//        configurer.addPathPrefix("/api/v2",
//                c -> c.isAnnotationPresent(ApiV2Controller.class));
    }
}