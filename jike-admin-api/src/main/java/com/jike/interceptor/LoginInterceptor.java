package com.jike.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.jike.config.AppConfig;
import com.jike.redis.RedisCacheUtil;
import com.jike.response.security.SecretConfig;
import com.jike.service.user.UserService;
import com.jike.utils.JwtTokenUtil;
import com.jjs.common.SecurityUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

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
        if (path.equals("/javacode/generate")
                || path.contains("/login")
                || path.contains("/logout")
                || path.contains("/sysinitpermission/init")
                || path.contains("/test")
                || path.contains("/user-payed-order-list")
                || isFrameworkErrorPath(path)) {
            return true;
        }

//        List<String> whiteList = RedisCacheUtil.getList("api:whiteList");
//        if (whiteList != null) {
//            for (String wPath : whiteList) {
//                if (path.startsWith(wPath)
//                ) {
//                    return true;
//                }
//            }
//        }

        // 从 http 请求头中取出签名
        String token = httpServletRequest.getHeader("x-token");

        // 设置响应Header
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");

        // userId 和 token未传
        if (token == null || token.isEmpty()) {
            responseMessage(httpServletResponse, ApiResponseUtil.getApiResponseParamsMissing("headers缺少必要参数"),true);
            return false;
        }
        if (!StpUtil.isLogin()) {
            responseMessage(httpServletResponse, ApiResponseUtil.getApiResponseAuthFailure("token无效，请重新登录"),true);
            return false;
        }

        // 对sa token 进行自动续期
        StpUtil.getLoginId();

        return true;
    }

    /**
     * 返回信息给客户端
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
}
