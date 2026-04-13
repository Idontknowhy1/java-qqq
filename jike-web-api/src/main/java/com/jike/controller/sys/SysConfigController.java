package com.jike.controller.sys;

import com.alibaba.fastjson.JSON;
import com.jike.model.sys.SysConfig;
import com.jike.redis.RedisCacheUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/sysconfig/v1")
@RestController
@Slf4j
public class SysConfigController {

    @GetMapping("/getconfig")
    public ApiResponse getConfig() {
        try {
            String configJsonString = RedisCacheUtil.getString("sys:config");
            SysConfig sysConfig = new SysConfig();
            if (configJsonString != null && !configJsonString.isEmpty()) {
                sysConfig = JSON.parseObject(configJsonString, SysConfig.class);
            }
            return ApiResponseUtil.getApiResponseSuccess(sysConfig);

        } catch (Exception ex) {
            log.error("getConfig," + ex.getMessage(), ex);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

}
