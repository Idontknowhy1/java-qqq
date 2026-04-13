package com.jike.utils;

import com.jike.mapper.user.UserMapper;
import com.jike.model.user.UserEntity;
import com.jike.request.AppRequestHeader;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeaderAuthChecker {
    @Autowired
    UserMapper userMapper;

    public ApiResponse check(AppRequestHeader header) {
        if (header.getToken().isEmpty() || header.getUserId().isEmpty()) {
            return ApiResponseUtil.getApiResponseParamsError("token无效");
        }

        UserEntity userEntity = userMapper.selectById(header.getUserId());
        if (userEntity == null) {
            return ApiResponseUtil.getApiResponseParamsError("token无效");
        }

        return null;
    }
}
