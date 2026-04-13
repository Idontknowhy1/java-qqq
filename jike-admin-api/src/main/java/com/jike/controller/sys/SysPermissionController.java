package com.jike.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.controller.sys.request.UpdateRolePermissionsReqeust;
import com.jike.model.sys.SysAccountEntity;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVO;
import com.jike.redis.RedisCacheUtil;
import com.jike.request.AppRequestHeader;
import com.jike.response.security.SecretConfig;
import com.jike.service.sys.SysAccountService;
import com.jike.service.user.UserService;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jike.model.sys.SysPermissionEntity;
import com.jike.model.sys.SysPermissionVO;
import com.jike.model.sys.SysPermissionDTO;
import com.jjs.common.ConvertUtil;
import com.jjs.common.SecurityUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.sys.SysPermissionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/syspermission/v1")
@RestController
@Slf4j
public class SysPermissionController {

    @Autowired
    private SecretConfig secretConfig;

    private final UserService userService;
    public SysPermissionController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    SysPermissionService service;
    @Autowired
    SysAccountService sysAccountService;

    /**
     * 保存、修改
     */
    @SaCheckPermission("permissions")
    @PostMapping("/save")
    public ApiResponse save(@RequestBody SysPermissionDTO obj) throws Exception {
        SysPermissionEntity entity = ConvertUtil.convertTo(obj, SysPermissionEntity.class);
        if (obj.getId() > 0) {
            service.updateById(entity);
        } else {
            service.save(entity);
        }
        entity = service.getById(entity.getId());
        SysPermissionVO vo = ConvertUtil.convertTo(entity, SysPermissionVO.class);
        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

    /**
     * 删除
     */
    @SaCheckPermission("permissions")
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
     * 获取角色权限列表
     */
    @SaCheckPermission("permissions")
    @GetMapping("/rolePermissions")
    public ApiResponse getRolePermission(@RequestParam int roleId) throws Exception {
        try {
            // 权限
            List<SysPermissionEntity> permissions = service.getRolePermissions(roleId);
            List<Integer> ids = new ArrayList<>();
            for (SysPermissionEntity permission : permissions) {
                ids.add(permission.getId());
            }
            return ApiResponseUtil.getApiResponseSuccess(ids);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 更新角色权限
     */
    @SaCheckPermission("permissions")
    @PostMapping("/updateRolePermission")
    public ApiResponse updateRolePermission(@RequestBody UpdateRolePermissionsReqeust request) throws Exception {
        try {
            service.updateRolePermissions(request.getRoleId(), request.getPermissionIds());
            RedisCacheUtil.del("role:permission:" + request.getRoleId());
            RedisCacheUtil.del("role:menus:" + request.getRoleId());
            return ApiResponseUtil.getApiResponseSuccess();

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取列表
     */
    @SaCheckPermission("permissions")
    @GetMapping("/list")
    public ApiResponse getList() throws Exception {
        try {
            LambdaQueryWrapper<SysPermissionEntity> queryWrapper = new QueryWrapper<SysPermissionEntity>().lambda()
                    .orderBy(true, true, SysPermissionEntity::getOrderIndex);
            List<SysPermissionVO> result = service.getList(queryWrapper);
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }


    /**
     * 获取用户登录后菜单列表
     */
//    @GetMapping("/userMenus")
//    public ApiResponse userMenus(AppRequestHeader header) throws Exception {
//        try {
//            UserVO userVO = userService.getUserById(header.getUserId());
//            if (userVO == null) {
//                return ApiResponseUtil.getApiResponseParamsError("userId 无效");
//            }
//            String redisKey = "role:menus:" + userVO.getRoleId();
//            List<String> items = RedisCacheUtil.getList(redisKey);
//            List<SysPermissionVO> menus;
//            if (items == null || items.isEmpty()) {
//                menus = service.getRoleMenus(userVO.getRoleId());
//                items = new ArrayList<>();
//                for (SysPermissionVO permission : menus) {
//                    items.add(JSON.toJSONString(permission));
//                }
//                RedisCacheUtil.setList(redisKey, items);
//            } else {
//                menus = new ArrayList<>();
//                for (String item : items) {
//                    menus.add(JSON.parseObject(item, SysPermissionVO.class));
//                }
//            }
//
//            return ApiResponseUtil.getApiResponseSuccess(new HashMap<String, Object>() {{
//                put("menus", menus);
//            }});
//
//
//        } catch(Exception ex){
//            log.error(this.getClass().getName() + " error" + ex,ex);
//            return ApiResponseUtil.getApiResponseFailure();
//        }
//    }

    /**
     * 获取用户权限列表
     */
    @GetMapping("/userPermissions")
    public ApiResponse getUserPermission() throws Exception {
        try {
            Object loginId = StpUtil.getLoginId();
            SysAccountEntity accountEntity = sysAccountService.getOne(new QueryWrapper<SysAccountEntity>().lambda()
                    .eq(SysAccountEntity::getId, loginId));
            if (accountEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("userId 无效");
            }

            int roleId = accountEntity.getRoleId();

            // 权限
            String redisKey = "role:permission:" + roleId;
            List<String> codes = RedisCacheUtil.getList(redisKey);
            if (codes == null || codes.isEmpty()) {
                // 权限
                List<SysPermissionEntity> permissions = service.getRolePermissions(roleId);
                codes = new ArrayList<>();
                for (SysPermissionEntity permission : permissions) {
                    codes.add(permission.getCode());
                }
                RedisCacheUtil.setList(redisKey, codes);
            }

            // 菜单
            redisKey = "role:menus:" + roleId;
            List<String> items = RedisCacheUtil.getList(redisKey);
            List<SysPermissionVO> menus;
            if (items == null || items.isEmpty()) {
                menus = service.getRoleMenus(roleId);
                items = new ArrayList<>();
                for (SysPermissionVO permission : menus) {
                    items.add(JSON.toJSONString(permission));
                }
                RedisCacheUtil.setList(redisKey, items);
            } else {
                menus = new ArrayList<>();
                for (String item : items) {
                    menus.add(JSON.parseObject(item, SysPermissionVO.class));
                }
            }

            List<String> finalCodes = codes;
            Map result = new HashMap<String, Object>() {{
                put("permissions", finalCodes);
                put("menus", menus);
            }};
            String jsonString = JSON.toJSONString(result);
            String ciphertext = SecurityUtil.aes.encrypt(jsonString, secretConfig.getKey());

            return ApiResponseUtil.getApiResponseSuccess(ciphertext);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
