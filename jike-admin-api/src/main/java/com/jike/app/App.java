package com.jike.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.jike", "com.jjs"})
@MapperScan({"com.jike.mapper", "com.jjs"})
@EnableTransactionManagement
public class App extends SpringBootServletInitializer {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }

    @PostConstruct
    void setDefaultTimeZone() {
        // 设置JVM默认时区为东八区（北京时间）
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
