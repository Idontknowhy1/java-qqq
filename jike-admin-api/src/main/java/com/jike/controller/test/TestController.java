package com.jike.controller.test;

import com.jjs.common.PageListResult;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

//    @GetMapping("/test")
//    public
//    @ResponseBody ApiResponse test() {
//
//        NanoTaskEntity taskEntity = new NanoTaskEntity();
//        taskEntity.setId(1000);
//        taskEntity.setValue("2200");
//        taskEntity.setCreateTimeV2(new Date());
//
//        List<NanoTaskEntity> list = new ArrayList<>();
//        list.add(taskEntity);
//        list.add(taskEntity);
//
//        PageListResult<NanoTaskEntity> pageResult = new PageListResult<>();
//        pageResult.setPageNum(10);
//        pageResult.setPageSize(10);
//        pageResult.setList(list);
//
//        PageListResult<NanoTaskVO> voPageListResult = GenericMapper.INSTANCE.convertToPageResult(pageResult);
//
//        return ApiResponseUtil.getApiResponseSuccess(voPageListResult);
//    }

}
