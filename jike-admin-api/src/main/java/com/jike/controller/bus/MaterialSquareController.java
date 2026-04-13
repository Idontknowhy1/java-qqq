package com.jike.controller.bus;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jike.model.bus.MaterialSquareEntity;
import com.jike.model.bus.MaterialSquareVO;
import com.jike.model.bus.MaterialSquareDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.bus.MaterialSquareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/materialsquare/v1")
@RestController
@Slf4j
public class MaterialSquareController {

    final
    MaterialSquareService service;
    public MaterialSquareController(MaterialSquareService service) { this.service = service; } 

    /**
      * 保存、修改
     */
    @PostMapping("/save")
    public ApiResponse save(@RequestBody MaterialSquareDTO obj) throws Exception {
        MaterialSquareEntity entity = ConvertUtil.convertTo(obj, MaterialSquareEntity.class);
        if (obj.getId() > 0) {
            service.updateById(entity);
        } else {
            service.save(entity);
        }
        entity = service.getById(entity.getId());
        MaterialSquareVO vo = ConvertUtil.convertTo(entity, MaterialSquareVO.class);
        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

    /**
     * 删除
     */
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
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute BasePageQuery query) throws Exception {
        try {
            LambdaQueryWrapper<MaterialSquareEntity> queryWrapper = new QueryWrapper<MaterialSquareEntity>().lambda()
                    .orderBy(true, false,MaterialSquareEntity::getCreateTime );
            PageListResult<MaterialSquareVO> result = service.getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
