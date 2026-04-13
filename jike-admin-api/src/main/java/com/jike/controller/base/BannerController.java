package com.jike.controller.base;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jike.model.base.BannerEntity;
import com.jike.model.base.BannerVO;
import com.jike.model.base.BannerDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.base.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/banners/v1")
@RestController
@Slf4j
public class BannerController {

    final
    BannerService service;
    public BannerController(BannerService service) { this.service = service; } 

    /**
      * 保存、修改
     */
    @SaCheckPermission("banners:save")
    @PostMapping("/save")
    public ApiResponse save(@RequestBody BannerDTO obj) throws Exception {
        BannerEntity entity = ConvertUtil.convertTo(obj, BannerEntity.class);
        if (obj.getId() > 0) {
            service.updateById(entity);
        } else {
            service.save(entity);
        }
        entity = service.getById(entity.getId());
        BannerVO vo = ConvertUtil.convertTo(entity, BannerVO.class);
        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

    /**
     * 删除
     */
    @SaCheckPermission("banners:delete")
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
    @SaCheckPermission("banners")
    @GetMapping("/list")
    public ApiResponse getList(@ModelAttribute BasePageQuery query) throws Exception {
        try {
            LambdaQueryWrapper<BannerEntity> queryWrapper = new QueryWrapper<BannerEntity>().lambda();
            //queryWrapper.eq(true, "name", "name47");
            List<BannerVO> list = new ArrayList<>();
            for (BannerEntity bannerEntity : service.list()) {
                BannerVO vo = ConvertUtil.convertTo(bannerEntity, BannerVO.class);
                vo.setId("" + bannerEntity.getId());
                list.add(vo);
            }
            return ApiResponseUtil.getApiResponseSuccess(list);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
