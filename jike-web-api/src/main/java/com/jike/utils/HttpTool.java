package com.jike.utils;

import cn.hutool.core.lang.Pair;
import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpTool {

    /**
     * 发送get请求
     *
     * @param url    地址
     * @param params 参数
     * @return 请求结果
     */
    public static String get(String url) {
        return request("get", url, null, null);
    }
    public static String get(String url, Map<String, Object> params) {
        return request("get", url, null, params);
    }
    public static String get(String url,Map<String, String> headers, Map<String, Object> params) {
        return request("get", url,headers, params);
    }

    /**
     * 发送post请求
     *
     * @param url    地址
     * @return 请求结果
     */
    public static String post(String url, Map<String, Object> params) {
        return request("post", url,null, params);
    }
    public static String post(String url,Map<String, String> headers, Map<String, Object> params) {
        return request("post", url,headers, params);
    }

    private static String request(String method, String url,Map<String, String> headers, Map<String, Object> params) {
        if (method == null) {
            throw new RuntimeException("请求方法不能为空");
        }

        if (url == null) {
            throw new RuntimeException("url不能为空");
        }

        Request request = null;
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        Request.Builder requestBuilder = new Request.Builder();

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        requestBuilder.addHeader("Accept", "application/json");
        requestBuilder.addHeader("Content-Type", "application/json");

            if ("get".equals(method)) {
                if (params != null) {
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), param.getValue().toString());
                    }
                }
                request = requestBuilder.url(httpBuilder.build()).build();

            } else {
                if (params == null) {
                    throw new RuntimeException("params不能为空");
                }
                request = requestBuilder.url(httpBuilder.build()).post(RequestBody.create(
                                JSON.toJSONString(params),
                                MediaType.parse("application/json")
                        ))
                        .build();
            }

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            Response response = client.newCall(request).execute();
            Headers responseHeaders = response.headers();

            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 发送post请求（json格式）
     *
     * @param url  url
     * @param json json字符串
     * @return 请求结果
     */
    public static String postJson(String url, String json) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.Companion.create(json, MediaType.Companion.parse("application/json")))
                .build();

        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }
}
