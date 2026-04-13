package com.jike.controller.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.PageListResult;
import com.jike.model.order.GoodsEntity;
import com.jike.model.order.GoodsVO;
import com.jike.model.order.GoodsDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.order.GoodsService;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

@RequestMapping("/goods/v1")
@RestController
@Slf4j
public class GoodsController extends ApiBaseControllerV2<GoodsEntity,GoodsVO, GoodsDTO> {

    final
    GoodsService service;
    public GoodsController(GoodsService service) { this.service = service; } 
    @Override
    public GoodsService getService() { return service; } 

    public long getDtoKey(GoodsDTO obj) {  return obj.getId();} 
    public long getEntityKey(GoodsEntity obj) {  return obj.getId();} 

    protected Class<GoodsVO> getVoClass() { return GoodsVO.class; }
    protected Class<GoodsDTO> getDtoClass() { return GoodsDTO.class; }
    protected Class<GoodsEntity> getEntityClass() { return GoodsEntity.class; } 

    /**
     * 开启的父类默认方法
     */
    @Override
    public List<DefaultMethod> enableDefaultMethods() {
        return Arrays.asList(
                DefaultMethod.SAVE,
                DefaultMethod.DELETE
        );
    }

    /**
     * 获取列表
     */
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute BasePageQuery query) throws Exception {
        try {
            LambdaQueryWrapper<GoodsEntity> queryWrapper = new QueryWrapper<GoodsEntity>().lambda();
            PageListResult<GoodsVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public ApiResponse getList(@ModelAttribute BasePageQuery query) throws Exception {
        try {
            LambdaQueryWrapper<GoodsEntity> queryWrapper = new QueryWrapper<GoodsEntity>().lambda();
            //queryWrapper.eq(true, "name", "name47");
            List<GoodsVO> result = this.getService().getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
