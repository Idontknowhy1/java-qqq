package com.jike.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.jike.config.AppConfig;
import com.jike.redis.RedisCacheUtil;
import com.jike.service.user.UserService;
import com.jike.utils.JwtTokenUtil;
import com.jjs.common.SecurityUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jjs.response.security.SecretConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import static cn.dev33.satoken.sign.SaSignTemplate.sign;

@Slf4j
@Configuration
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;
    @Autowired
    SecretConfig secretConfig;
    @Autowired
    AppConfig appConfig;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {

        // 校验白名单
        String path = httpServletRequest.getServletPath();

        // 判断是否是框架错误路径，如果是，则跳过鉴权逻辑
        if (path.equals("/javacode/generate") || isFrameworkErrorPath(path)) {
            return true;
        }

        // 设置响应Header
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");

        // 服务内部接口，需严格校验token
        if (path.startsWith("/inner/")) {
            // 从 http 请求头中取出签名
            String sign = httpServletRequest.getHeader("sign");
            String key = httpServletRequest.getHeader("key");
            if (key == null || sign == null) {
                responseMessage(httpServletResponse, ApiResponseUtil.getApiResponseAuthFailure("验证签名失败"),true);
                return false;
            }

            String redisKey = "inner:" + key;
            String redisValue = RedisCacheUtil.getString(redisKey);
            String redisValueMD5 = SecurityUtil.md5.md5(redisValue);
            if (!sign.equals(redisValueMD5)) {
                responseMessage(httpServletResponse, ApiResponseUtil.getApiResponseAuthFailure("验证签名失败2"),true);
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }

    /**
     * 返回信息给客户端
     *
     * @param response
     * @param out
     * @param apiResponse
     */
    private void responseMessage(HttpServletResponse response, ApiResponse apiResponse, boolean secret) {
        response.setContentType("application/json; charset=utf-8");
        try {
            String json = JSON.toJSONString(apiResponse);
            if (secret && secretConfig.getEnabled()) {
                json = SecurityUtil.aes.encrypt(json, secretConfig.getKey());
            }
            PrintWriter writer = response.getWriter();
            writer.print(json);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            log.error("error",e);
        }
    }

    // 判断是否是框架错误路径
    private boolean isFrameworkErrorPath(String path) {
        // 根据实际情况判断框架错误的路径
        return path.equals("/error") || path.equals("/error-page");
    }


//    @Override
//    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
//
//        // 校验白名单
//        String path = httpServletRequest.getServletPath();
//
//        // 判断是否是框架错误路径，如果是，则跳过鉴权逻辑
//        if (path.equals("/javacode/generate") || isFrameworkErrorPath(path)) {
//            return true;
//        }
//
//
//        // 测试环境直接通过
//        if (appConfig.isProductEnv() == false) {
//            return true;
//        }
//
//        List<String> whiteList = RedisCacheUtil.getList("api:whiteList");
//        if (whiteList != null) {
//            for (String wPath : whiteList) {
//                if (path.startsWith(wPath)
//                ) {
//                    return true;
//                }
//            }
//        }
//
//        // 从 http 请求头中取出签名
//        String token = httpServletRequest.getHeader("token");
//        String userIdStr = httpServletRequest.getHeader("userId");
//
//        // 设置响应Header
//        httpServletResponse.setCharacterEncoding("UTF-8");
//        httpServletResponse.setContentType("application/json; charset=utf-8");
//
//        // userId 和 token未传
//        if (token == null || token.isEmpty()
//                || userIdStr == null || userIdStr.isEmpty()) {
//            responseMessage(httpServletResponse, ApiResponseUtil.getApiResponseParamsMissing("headers缺少必要参数"),true);
//            return false;
//        }
//
//        String redisKey = "miniuser:token:" + userIdStr;
//
//        try {
//            Integer userId = Integer.parseInt(userIdStr);
//
//            String redisToken = RedisCacheUtil.getString(redisKey);
//            if (redisToken == null || redisToken.isEmpty() || !redisToken.equals(token)) {
//                responseMessage(httpServletResponse, ApiResponseUtil.getApiResponseAuthFailure("token无效,请重新登录"),false);
//                return false;
//            }
//
//        } catch (Exception ex) {
//            log.error("LoginInterceptor失败",ex);
//        }
//
//        // 对sa token 进行自动续期
//        if (StpUtil.isLogin()) {
//            StpUtil.getLoginId();
//        }
//
//        return true;
//    }

}
