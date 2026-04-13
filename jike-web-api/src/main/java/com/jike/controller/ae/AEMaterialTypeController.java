package com.jike.controller.ae;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.controller.ae.request.AEMaterialTypeListRequest;
import com.jike.model.ae.AEMaterialEntity;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.PageListResult;
import com.jike.model.ae.AEMaterialTypeEntity;
import com.jike.model.ae.AEMaterialTypeVO;
import com.jike.model.ae.AEMaterialTypeDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.ae.AEMaterialTypeService;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

@RequestMapping("/aematerialtype/v1")
@RestController
@Slf4j
public class AEMaterialTypeController extends ApiBaseControllerV2<AEMaterialTypeEntity,AEMaterialTypeVO, AEMaterialTypeDTO> {

    final
    AEMaterialTypeService service;
    public AEMaterialTypeController(AEMaterialTypeService service) { this.service = service; } 
    @Override
    public AEMaterialTypeService getService() { return service; } 

    public long getDtoKey(AEMaterialTypeDTO obj) {  return obj.getId();} 
    public long getEntityKey(AEMaterialTypeEntity obj) {  return obj.getId();} 

    protected Class<AEMaterialTypeVO> getVoClass() { return AEMaterialTypeVO.class; }
    protected Class<AEMaterialTypeDTO> getDtoClass() { return AEMaterialTypeDTO.class; }
    protected Class<AEMaterialTypeEntity> getEntityClass() { return AEMaterialTypeEntity.class; } 

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
    @GetMapping("/list")
    public ApiResponse getList(@ModelAttribute AEMaterialTypeListRequest query) throws Exception {
        try {
            LambdaQueryWrapper<AEMaterialTypeEntity> queryWrapper = new QueryWrapper<AEMaterialTypeEntity>().lambda()
                    .eq(AEMaterialTypeEntity::getModule, query.getModule())
                    .orderBy(true, true, AEMaterialTypeEntity::getOrderIndex);
            List<AEMaterialTypeVO> result = this.getService().getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
