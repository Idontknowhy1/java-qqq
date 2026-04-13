package com.jike.controller.ae;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.controller.ae.request.AEMaterialPageListRequest;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.PageListResult;
import com.jike.model.ae.AEMaterialEntity;
import com.jike.model.ae.AEMaterialVO;
import com.jike.model.ae.AEMaterialDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.ae.AEMaterialService;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

@RequestMapping("/aematerial/v1")
@RestController
@Slf4j
public class AEMaterialController extends ApiBaseControllerV2<AEMaterialEntity,AEMaterialVO, AEMaterialDTO> {

    final
    AEMaterialService service;
    public AEMaterialController(AEMaterialService service) { this.service = service; } 
    @Override
    public AEMaterialService getService() { return service; } 

    public long getDtoKey(AEMaterialDTO obj) {  return obj.getId();} 
    public long getEntityKey(AEMaterialEntity obj) {  return obj.getId();} 

    protected Class<AEMaterialVO> getVoClass() { return AEMaterialVO.class; }
    protected Class<AEMaterialDTO> getDtoClass() { return AEMaterialDTO.class; }
    protected Class<AEMaterialEntity> getEntityClass() { return AEMaterialEntity.class; } 

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
    public ApiResponse getPageList(@ModelAttribute AEMaterialPageListRequest query) throws Exception {
        try {
            LambdaQueryWrapper<AEMaterialEntity> queryWrapper = new QueryWrapper<AEMaterialEntity>().lambda()
                    .eq(AEMaterialEntity::getTypeId, query.getTypeId())
                    .orderBy(true, true, AEMaterialEntity::getOrderIndex);
            PageListResult<AEMaterialVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
