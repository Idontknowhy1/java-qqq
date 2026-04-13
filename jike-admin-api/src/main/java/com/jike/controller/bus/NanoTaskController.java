package com.jike.controller.bus;

import cn.hutool.core.date.DateUtil;
import com.jike.controller.bus.request.NanoPageListRequest;
import com.jjs.common.PageListResult;
import com.jike.model.bus.NanoTaskVO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.bus.NanoTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/nanotasks/v1")
@RestController
@Slf4j
public class NanoTaskController {

    final
    NanoTaskService service;
    public NanoTaskController(NanoTaskService service) { this.service = service; }

    /**
     * 获取列表
     */
    @GetMapping("/page-list")
    public ApiResponse getAdminPageList(@ModelAttribute NanoPageListRequest request) throws Exception {
        try {

            PageListResult<NanoTaskVO> result = service.getPageList(request.getPageNum(), request.getPageSize(), request.getUuid());

            for (NanoTaskVO vo : result.getList()) {
                String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
                vo.setCreateTimeText(text);
            }

            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
