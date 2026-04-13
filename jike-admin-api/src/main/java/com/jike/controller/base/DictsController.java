package com.jike.controller.base;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.model.base.*;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.base.DictsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/dicts/v1")
@RestController
@Slf4j
public class DictsController {

    final
    DictsService service;
    public DictsController(DictsService service) { this.service = service; }

    /**
      * 保存、修改
     */
    @SaCheckPermission("xxxxxx:save")
    @PostMapping("/save")
    public ApiResponse save(@RequestBody DictsTypeDTO obj) throws Exception {
        DictsTypeEntity entity = ConvertUtil.convertTo(obj, DictsTypeEntity.class);
        if (obj.getId() > 0) {
            service.updateById(entity);
        } else {
            service.save(entity);
        }
        entity = service.getById(entity.getId());
        DictsTypeVO vo = ConvertUtil.convertTo(entity, DictsTypeVO.class);
        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

    /**
     * 删除
     */
    @SaCheckPermission("xxxxxx:delete")
    @GetMapping("/delete")
    public ApiResponse delete(@RequestParam String id) throws Exception {
        try {
            service.removeById(id);
            return ApiResponseUtil.getApiResponseSuccess("删除成功",null);
        }catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
           return ApiResponseUtil.getApiResponseFailure();
       }
    }

    /**
     * 获取列表
     */
    @GetMapping("/type-list")
    public ApiResponse getList() throws Exception {
        try {
            LambdaQueryWrapper<DictsTypeEntity> queryWrapper = new QueryWrapper<DictsTypeEntity>().lambda();
            List<DictsTypeVO> result = service.getTypeList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
    /**
     * 获取列表
     */
    @GetMapping("/detail-list")
    public ApiResponse getDetailList(@RequestParam String type) throws Exception {
        try {
            LambdaQueryWrapper<DictsDetailEntity> queryWrapper = new QueryWrapper<DictsDetailEntity>().lambda()
                    .eq(DictsDetailEntity::getTypeCode, type)
                    .orderBy(true ,true, DictsDetailEntity::getOrderIndex);
            List<DictsDetailVO> result = service.getDetailList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
