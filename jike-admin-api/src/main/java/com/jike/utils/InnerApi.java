package com.jike.utils;

import com.alibaba.fastjson.JSON;
import com.jike.redis.RedisCacheUtil;
import com.jjs.common.HashMapUtil;
import com.jjs.common.SecurityUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

import static cn.dev33.satoken.sign.SaSignTemplate.key;

@Component
@Slf4j
public class InnerApi {

    @Value("${app.innerUrl}")
    private String innerUrl;


    public Object sendPost(String url, HashMap<String, Object> params) {
        try {
            SignResult signResult = getSign();
            String result = HttpTool.post(innerUrl + url, new HashMap<String, String>() {{
                put("key", signResult.getKey());
                put("sign", signResult.getSign());
            }}, params);

            return afterRequest(result);

        } catch (Exception ex) {
            log.error("-- url = " + url + " params = " + JSON.toJSONString(params), ex);
        }
        return null;
    }

    public Object sendGet(String url, HashMap<String, Object> params) {
        try {
            SignResult signResult = getSign();
            String result = HttpTool.get(innerUrl + url, new HashMap<String, String>() {{
                put("key", signResult.getKey());
                put("sign", signResult.getSign());
            }}, params);

            return afterRequest(result);

        } catch (Exception ex) {
            log.error("-- url = " + url + " params = " + JSON.toJSONString(params), ex);
        }
        return null;
    }


    private Object afterRequest(String result) {
        if (result == null) {
            return ApiResponseUtil.getApiResponseFailure("未找到service");
        }

        HashMap resultMap = HashMapUtil.hashMapFromJson(result);
        if (resultMap.containsKey("status") && (Integer)resultMap.get("status") == 500) {
            return ApiResponseUtil.getApiResponseFailure((String) resultMap.get("error"));
        }

        if (resultMap.containsKey("code")) {
            if ((Integer)resultMap.get("code") == 99999) {
                return ApiResponseUtil.getApiResponseFailure((String) resultMap.get("service 内部错误"));
            }
            return JSON.parseObject(result, ApiResponse.class);
        }

        return resultMap;
    }

    private SignResult getSign() throws Exception{
        String key = UUID.randomUUID().toString();
        String token = JwtTokenUtil.generateToken(key);
        String md5 = SecurityUtil.md5.md5(token);
        RedisCacheUtil.setString("inner:" + key, token, 10);

        return new SignResult(key, md5);
    }

    @Data
    @AllArgsConstructor
    private static class SignResult {
        private String key;
        private String sign;
    }
}
