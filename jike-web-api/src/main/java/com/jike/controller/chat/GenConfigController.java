package com.jike.controller.chat;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jike.model.chat.GenConfigEntity;
import com.jike.model.chat.GenConfigVO;
import com.jike.model.chat.GenConfigDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.chat.GenConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/genconfig/v1")
@RestController
@Slf4j
public class GenConfigController {

    final
    GenConfigService service;
    public GenConfigController(GenConfigService service) { this.service = service; } 

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public ApiResponse getList() throws Exception {
        try {
            LambdaQueryWrapper<GenConfigEntity> queryWrapper = new QueryWrapper<GenConfigEntity>().lambda();
            List<GenConfigVO> result = service.getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}

