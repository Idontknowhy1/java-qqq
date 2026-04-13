package com.jike.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jjs.common.ConvertUtil;
import com.jike.model.sys.SysRoleEntity;
import com.jike.model.sys.SysRoleVO;
import com.jike.model.sys.SysRoleDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.sys.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/sysrole/v1")
@RestController
@Slf4j
public class SysRoleController {

    final
    SysRoleService service;
    public SysRoleController(SysRoleService service) { this.service = service; }

    /**
     * 保存、修改
     */
    @SaCheckPermission("roles")
    @PostMapping("/save")
    public ApiResponse save(@RequestBody SysRoleDTO obj) throws Exception {
        SysRoleEntity entity = ConvertUtil.convertTo(obj, SysRoleEntity.class);
        if (obj.getId() > 0) {
            service.updateById(entity);
        } else {
            service.save(entity);
        }
        entity = service.getById(entity.getId());
        SysRoleVO vo = ConvertUtil.convertTo(entity, SysRoleVO.class);
        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

    /**
     * 删除
     */
    @SaCheckPermission("roles:delete")
    @GetMapping("/delete")
    public ApiResponse delete(@RequestParam Integer id) throws Exception {
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
    @GetMapping("/list")
    @SaCheckPermission("roles")
    public ApiResponse getList() throws Exception {
        try {
            LambdaQueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<SysRoleEntity>().lambda();
            //queryWrapper.eq(true, "name", "name47");
            List<SysRoleVO> result = service.getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
