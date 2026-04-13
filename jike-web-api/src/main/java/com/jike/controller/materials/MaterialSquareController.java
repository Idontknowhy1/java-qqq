package com.jike.controller.materials;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.model.materials.MaterialSquareEntity;
import com.jike.model.materials.MaterialSquareVO;
import com.jike.model.materials.MaterialSquareDTO;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.DefaultMethod;
import com.jjs.common.PageListResult;
import com.jike.service.materials.MaterialSquareService;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Arrays;

@RequestMapping("/materialsquare/v1")
@RestController
@Slf4j
public class MaterialSquareController extends ApiBaseControllerV2<MaterialSquareEntity,MaterialSquareVO, MaterialSquareDTO> {

    final
    MaterialSquareService service;
    public MaterialSquareController(MaterialSquareService service) { this.service = service; } 
    @Override
    public MaterialSquareService getService() { return service; } 

    public long getDtoKey(MaterialSquareDTO obj) {  return obj.getId();} 
    public long getEntityKey(MaterialSquareEntity obj) {  return obj.getId();} 

    protected Class<MaterialSquareVO> getVoClass() { return MaterialSquareVO.class; }
    protected Class<MaterialSquareDTO> getDtoClass() { return MaterialSquareDTO.class; }
    protected Class<MaterialSquareEntity> getEntityClass() { return MaterialSquareEntity.class; } 

    /**
     * 开启的父类默认方法ÏÏ
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
            QueryWrapper<MaterialSquareEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderBy(true, false, "id");
            PageListResult<MaterialSquareVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);

            // format create time
            for (MaterialSquareVO vo : result.getList()) {
                String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
                vo.setCreateTimeText(text);
            }

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
            QueryWrapper<MaterialSquareEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderBy(true, false, "id");
            //queryWrapper.eq(true, "name", "name47");
            List<MaterialSquareVO> result = this.getService().getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
