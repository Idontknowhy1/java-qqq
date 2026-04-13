package com.jike.controller.nano;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.PageListResult;
import com.jike.model.nano.NanoTaskHpEntity;
import com.jike.model.nano.NanoTaskHpVO;
import com.jike.model.nano.NanoTaskHpDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.nano.NanoTaskHpService;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

@RequestMapping("/nanotaskhp/v1")
@RestController
@Slf4j
public class NanoTaskHpController extends ApiBaseControllerV2<NanoTaskHpEntity,NanoTaskHpVO, NanoTaskHpDTO> {

    final
    NanoTaskHpService service;
    public NanoTaskHpController(NanoTaskHpService service) { this.service = service; } 
    @Override
    public NanoTaskHpService getService() { return service; } 

    public long getDtoKey(NanoTaskHpDTO obj) {  return obj.getId();} 
    public long getEntityKey(NanoTaskHpEntity obj) {  return obj.getId();} 

    protected Class<NanoTaskHpVO> getVoClass() { return NanoTaskHpVO.class; }
    protected Class<NanoTaskHpDTO> getDtoClass() { return NanoTaskHpDTO.class; }
    protected Class<NanoTaskHpEntity> getEntityClass() { return NanoTaskHpEntity.class; } 

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
            LambdaQueryWrapper<NanoTaskHpEntity> queryWrapper = new QueryWrapper<NanoTaskHpEntity>().lambda();
            PageListResult<NanoTaskHpVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
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
            LambdaQueryWrapper<NanoTaskHpEntity> queryWrapper = new QueryWrapper<NanoTaskHpEntity>().lambda();
            //queryWrapper.eq(true, "name", "name47");
            List<NanoTaskHpVO> result = this.getService().getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
