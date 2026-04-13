package com.jike.controller.utils;

import cn.hutool.extra.tokenizer.Result;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.jike.utils.OssConfig;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/oss")
@Slf4j
public class OssController {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    /**
     * 获取OSS上传策略和签名
     */
    @GetMapping("/policy")
    public ApiResponse getOssPolicy() {
        try {
            // 设置过期时间
            long expireEndTime = System.currentTimeMillis() + ossConfig.getExpireTime() * 1000;
            Date expiration = new Date(expireEndTime);

            // 设置上传目录（按日期分类）
            String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";

            // 创建策略条件
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, ossConfig.getMaxSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            // 生成策略
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("accessKeyId", ossConfig.getAccessKeyId());
            result.put("policy", encodedPolicy);
            result.put("signature", postSignature);
            result.put("dir", dir);
            result.put("host", ossConfig.getHost());
            result.put("expire", String.valueOf(expireEndTime / 1000));

            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch (Exception e) {
            log.error("获取OSS上传策略失败", e);
            return ApiResponseUtil.getApiResponseParamsError("获取上传凭证失败");
        }
    }

    /**
     * 使用STS临时凭证方案（更安全）
     */
    @GetMapping("/sts-token")
    public ApiResponse getStsToken() {
        // 实现STS Token获取逻辑[1,4](@ref)
        // 这种方式更安全，推荐生产环境使用
        return ApiResponseUtil.getApiResponseSuccess("");
    }

    public static void main(String[] args) {
        // 从环境变量获取访问凭证
        String accessKeyId = "LTAI5tNPmHXr9bspBUWsxFte";
        String accessKeySecret = "Sfrkix9uUkv3IWu8cjwXweDoIpBiSz";

        // 设置OSS地域和Endpoint
        String region = "cn-hangzhou";
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
//        String bucketName = "jikeing";

        // 创建凭证提供者
        DefaultCredentialProvider provider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // 配置客户端参数
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 显式声明使用V4签名算法
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .credentialsProvider(provider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .endpoint(endpoint)
                .build();

        // 列出当前用户的所有Bucket
        List<Bucket> buckets = ossClient.listBuckets();
        System.out.println("成功连接到OSS服务，当前账号下的Bucket列表：");

        if (buckets.isEmpty()) {
            System.out.println("当前账号下暂无Bucket");
        } else {
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.getName());
            }
        }
        // 释放资源
        ossClient.shutdown();
        System.out.println("OSS客户端已关闭");
    }
}
