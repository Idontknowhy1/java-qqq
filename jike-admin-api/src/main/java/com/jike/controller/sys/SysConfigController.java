package com.jike.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.controller.sys.request.ChangePwdRequest;
import com.jike.controller.user.request.LoginRequest;
import com.jike.model.sys.SysAccountDTO;
import com.jike.model.sys.SysAccountEntity;
import com.jike.model.sys.SysAccountVO;
import com.jike.model.sys.SysConfig;
import com.jike.redis.RedisCacheUtil;
import com.jike.service.sys.SysAccountService;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

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

    @PostMapping("/saveconfig")
    public ApiResponse saveConfig(@RequestBody SysConfig config) {
        try {
            RedisCacheUtil.setString("sys:config", JSON.toJSONString(config));
            return ApiResponseUtil.getApiResponseSuccess();

        } catch (Exception ex) {
            log.error("saveConfig," + ex.getMessage(), ex);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

}
