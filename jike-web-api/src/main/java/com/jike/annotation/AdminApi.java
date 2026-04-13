package com.jike.annotation;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

// 管理员接口分组注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController  // 组合了@RestController的功能
public @interface AdminApi {
    String value() default ""; // 可用于更细粒度的命名
}