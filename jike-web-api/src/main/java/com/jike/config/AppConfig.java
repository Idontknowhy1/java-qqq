package com.jike.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${app.env:dev}")
    private String env;

    /**
     * 是否是线上环境的判断
     */
    public boolean isProductEnv() {
       return "prod".equals(env);
    }

}
