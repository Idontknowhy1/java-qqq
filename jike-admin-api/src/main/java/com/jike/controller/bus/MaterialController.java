package com.jike.controller.bus;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.controller.bus.request.MaterialPageListRequest;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jike.model.bus.MaterialEntity;
import com.jike.model.bus.MaterialVO;
import com.jike.model.bus.MaterialDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.bus.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/materials/v1")
@RestController
@Slf4j
public class MaterialController {

    final
    MaterialService service;
    public MaterialController(MaterialService service) { this.service = service; } 

    /**
      * 保存、修改
     */
    @SaCheckPermission("materials:save")
    @PostMapping("/save")
    public ApiResponse save(@RequestBody MaterialDTO obj) throws Exception {
        MaterialEntity entity = ConvertUtil.convertTo(obj, MaterialEntity.class);
        if (obj.getId() > 0) {
            service.updateById(entity);
        } else {
            service.save(entity);
        }
        entity = service.getById(entity.getId());
        MaterialVO vo = ConvertUtil.convertTo(entity, MaterialVO.class);
        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

    /**
     * 删除
     */
    @SaCheckPermission("materials:delete")
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
       * 查看详情
      */
    @SaCheckPermission("materials")
    @GetMapping("/detail")
    public ApiResponse getDetail(@RequestParam String id) throws Exception {
         try {
             MaterialEntity obj = service.getOne(new QueryWrapper<MaterialEntity>().lambda().eq(MaterialEntity::getId, id));
             if (obj != null) {
                 MaterialVO vo = ConvertUtil.convertTo(obj, MaterialVO.class);
                return ApiResponseUtil.getApiResponseSuccess(vo);
            } else {
                return ApiResponseUtil.getApiResponseSuccess(null);
             }
         }catch(Exception ex){
             log.error(this.getClass().getName() + " error" + ex,ex);
             return ApiResponseUtil.getApiResponseFailure();
         }
     }

    /**
     * 获取列表
     */
    @SaCheckPermission("materials")
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute MaterialPageListRequest query) throws Exception {
        try {
            PageListResult<MaterialVO> result = service.getPageList(query.getPageNum(), query.getPageSize(), query.getCategoryId());
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
