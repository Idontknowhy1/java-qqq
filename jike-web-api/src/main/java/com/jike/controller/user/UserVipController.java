package com.jike.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.PageListResult;
import com.jike.model.user.UserVipEntity;
import com.jike.model.user.UserVipVO;
import com.jike.model.user.UserVipDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.user.UserVipService;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

@RequestMapping("/uservip/v1")
@RestController
@Slf4j
public class UserVipController extends ApiBaseControllerV2<UserVipEntity,UserVipVO, UserVipDTO> {

    final
    UserVipService service;
    public UserVipController(UserVipService service) { this.service = service; } 
    @Override
    public UserVipService getService() { return service; } 

    public long getDtoKey(UserVipDTO obj) {  return obj.getId();} 
    public long getEntityKey(UserVipEntity obj) {  return obj.getId();} 

    protected Class<UserVipVO> getVoClass() { return UserVipVO.class; }
    protected Class<UserVipDTO> getDtoClass() { return UserVipDTO.class; }
    protected Class<UserVipEntity> getEntityClass() { return UserVipEntity.class; } 

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
            LambdaQueryWrapper<UserVipEntity> queryWrapper = new QueryWrapper<UserVipEntity>().lambda();
            PageListResult<UserVipVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
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
            LambdaQueryWrapper<UserVipEntity> queryWrapper = new QueryWrapper<UserVipEntity>().lambda();
            //queryWrapper.eq(true, "name", "name47");
            List<UserVipVO> result = this.getService().getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
