package com.jike.response.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "secret")
@Setter
@Getter
public class SecretConfig {
    private Boolean enabled ;
    private String key ;
}
