package com.jike.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.controller.sys.request.ChangePwdRequest;
import com.jike.controller.user.request.LoginRequest;
import com.jike.model.user.UserEntity;
import com.jike.utils.JwtTokenUtil;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jike.model.sys.SysAccountEntity;
import com.jike.model.sys.SysAccountVO;
import com.jike.model.sys.SysAccountDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.sys.SysAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/sysaccount/v1")
@RestController
@Slf4j
public class SysAccountController {

    final
    SysAccountService service;
    public SysAccountController(SysAccountService service) { this.service = service; }

    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            SysAccountEntity userEntity = service.getOne(new QueryWrapper<SysAccountEntity>().lambda()
                    .eq(SysAccountEntity::getAccount, request.getAccount())
                    .eq(SysAccountEntity::getPassword, request.getPassword()));
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("用户名或密码错误");
            }

            // 先清理数据
            StpUtil.logout(userEntity.getId(), "PC");
            // 再登录
            SaLoginModel saLoginModel = new SaLoginModel();
            saLoginModel.setExtra("roleId", userEntity.getRoleId());
            saLoginModel.setDevice("PC");
            StpUtil.login(userEntity.getId(), saLoginModel);

            return ApiResponseUtil.getApiResponseSuccess(new HashMap<>(){{
                put("token", StpUtil.getTokenValue());
            }});

        } catch (Exception ex) {
            log.error("login," + ex.getMessage(), ex);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    @GetMapping("/logout")
    public ApiResponse logout() {
        try {
            if (StpUtil.isLogin()) {
                StpUtil.logout();
            }
            return ApiResponseUtil.getApiResponseSuccess();

        } catch (Exception ex) {
            log.error("logout," + ex.getMessage(), ex);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    @PostMapping("/changePwd")
    public ApiResponse changePwd(@Valid @RequestBody ChangePwdRequest request) {
        try {

            SysAccountEntity userEntity = service.getOne(new QueryWrapper<SysAccountEntity>().lambda()
                    .eq(SysAccountEntity::getId, StpUtil.getLoginId()));
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("密码修改失败");
            }

            if (!userEntity.getPassword().equals(request.getOldPwd())) {
                return ApiResponseUtil.getApiResponseParamsError("原密码错误");
            }

            userEntity.setPassword(request.getNewPwd());
            service.updateById(userEntity);

            // 修改密码后，退出登录
            StpUtil.logout(userEntity.getId(), "PC");

            return ApiResponseUtil.getApiResponseSuccess();

        } catch (Exception ex) {
            log.error("login," + ex.getMessage(), ex);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    /**
      * 保存、修改
     */
    @SaCheckPermission("accounts:save")
    @PostMapping("/save")
    public ApiResponse save(@RequestBody SysAccountDTO obj) throws Exception {
        SysAccountEntity entity = ConvertUtil.convertTo(obj, SysAccountEntity.class);
        if (obj.getId() > 0) {
            service.updateById(entity);
        } else {
            service.save(entity);
        }
        entity = service.getById(entity.getId());
        SysAccountVO vo = ConvertUtil.convertTo(entity, SysAccountVO.class);
        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

    /**
     * 删除
     */
    @SaCheckPermission("accounts:delete")
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
       * 查看详情
      */
    @SaCheckPermission("accounts")
    @GetMapping("/detail")
    public ApiResponse getDetail(@RequestParam String id) throws Exception {
         try {
             SysAccountEntity obj = service.getOne(new QueryWrapper<SysAccountEntity>().lambda().eq(SysAccountEntity::getId, id));
             if (obj != null) {
                 SysAccountVO vo = ConvertUtil.convertTo(obj, SysAccountVO.class);
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
    @SaCheckPermission("accounts")
    @GetMapping("/list")
    public ApiResponse getList(@ModelAttribute BasePageQuery query) throws Exception {
        try {
            LambdaQueryWrapper<SysAccountEntity> queryWrapper = new QueryWrapper<SysAccountEntity>().lambda();
            //queryWrapper.eq(true, "name", "name47");
            List<SysAccountVO> result = service.getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
