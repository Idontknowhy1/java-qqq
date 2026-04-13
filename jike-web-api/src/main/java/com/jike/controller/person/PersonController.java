package com.jike.controller.person;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.PageListResult;
import com.jike.model.person.PersonEntity;
import com.jike.model.person.PersonVO;
import com.jike.model.person.PersonDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.person.PersonService;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

@RequestMapping("/person/v1")
@RestController
@Slf4j
public class PersonController extends ApiBaseControllerV2<PersonEntity,PersonVO, PersonDTO> {

    final
    PersonService service;
    public PersonController(PersonService service) { this.service = service; } 
    @Override
    public PersonService getService() { return service; } 

    public long getDtoKey(PersonDTO obj) {  return obj.getId();} 
    public long getEntityKey(PersonEntity obj) {  return obj.getId();} 

    protected Class<PersonVO> getVoClass() { return PersonVO.class; }
    protected Class<PersonDTO> getDtoClass() { return PersonDTO.class; }
    protected Class<PersonEntity> getEntityClass() { return PersonEntity.class; } 

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
            LambdaQueryWrapper<PersonEntity> queryWrapper = new QueryWrapper<PersonEntity>().lambda();
            PageListResult<PersonVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
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
            LambdaQueryWrapper<PersonEntity> queryWrapper = new QueryWrapper<PersonEntity>().lambda();
            //queryWrapper.eq(true, "name", "name47");
            List<PersonVO> result = this.getService().getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
