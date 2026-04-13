package com.jike.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class OssConfig {
    @Value("${aliyunOss.endpoint}")
    private String endpoint;

    @Value("${aliyunOss.bucket}")
    private String bucket;

    @Value("${aliyunOss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyunOss.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aliyunOss.expire-time}")
    private long expireTime;

    @Value("${aliyunOss.max-size}")
    private long maxSize;

    @Value("${aliyunOss.host}")
    private String host;

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, secretAccessKey);
    }
}
