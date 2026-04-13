package com.jike.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.constant.PermissionsType;
import com.jike.controller.sys.request.UpdateRolePermissionsReqeust;
import com.jike.mapper.sys.SysRolePermissionRelMapper;
import com.jike.model.sys.*;
import com.jike.redis.RedisCacheUtil;
import com.jike.service.sys.SysAccountService;
import com.jike.service.sys.SysPermissionService;
import com.jike.service.sys.SysRoleService;
import com.jike.service.user.UserService;
import com.jjs.common.ConvertUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/sysinitpermission")
@RestController
@Slf4j
public class SysInitPermissionController {

    private final UserService userService;
    public SysInitPermissionController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    SysPermissionService service;
    @Autowired
    SysAccountService sysAccountService;
    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    SysRolePermissionRelMapper sysRolePermissionRelMapper;

    @GetMapping("/init")
    public ApiResponse initPermission() {

        String MENU = PermissionsType.MENU.getCode();
        String FUNC = PermissionsType.FUNC.getCode();

        // 保存“超级管理员”角色
        SysRoleEntity roleEntity = new SysRoleEntity();
        roleEntity.setName("超级管理员");
        sysRoleService.save(roleEntity);

        // 设置超级管理员账号
        SysAccountEntity accountEntity = new SysAccountEntity();
        accountEntity.setAccount("super");
        accountEntity.setPassword("14e1b600b1fd579f47433b88e8d85291"); // “123456”两次md5后的值
        accountEntity.setRoleId(roleEntity.getId());
        sysAccountService.save(accountEntity);

        Menu[] menus = new Menu[]{
                // 系统设置
                new Menu(MENU, "系统设置", "sys", null, new Menu[]{
                        new Menu(MENU, "功能点管理", "permissions", "/sys-permissions", new Menu[]{
                        }),
                        new Menu(MENU, "角色管理", "roles", "/sys-roles", new Menu[]{
                        })
                }),

                // 基础数据
                new Menu(MENU, "基础数据", "base", null, new Menu[]{
                        new Menu(MENU, "用户管理", "users", "/users", new Menu[]{
                        }),
                        new Menu(MENU, "用户会员", "user-vip", "/user-vip", new Menu[]{
                        }),
                        new Menu(MENU, "用户积分", "user-score", "/user-score", new Menu[]{
                        }),
                }),

                // 业务数据
                new Menu(MENU, "业务数据", "bus", null, new Menu[]{
                        new Menu(MENU, "素材管理", "materials", "/materials", new Menu[]{
                                new Menu(FUNC, "素材管理-保存", "materials:save", null),
                                new Menu(FUNC, "素材管理-删除", "materials:delete", null),
                        }),
                        new Menu(MENU, "广告位管理", "banners", "/banners", new Menu[]{
                                new Menu(FUNC, "广告位管理-保存", "banners:save", null),
                                new Menu(FUNC, "广告位管理-删除", "banners:delete", null),
                        }),
                        new Menu(MENU, "AI任务列表", "ai-images", "/ai-images", new Menu[]{
                        }),
                        new Menu(MENU, "素材广场", "squares", "/squares", new Menu[]{
                                new Menu(FUNC, "素材广场-删除", "squares:delete", null),
                        }),
                        new Menu(MENU, "上传拆分审核", "image-split", "/image-split-review", new Menu[]{
                        }),
                }),

                // 订单数据
                new Menu(MENU, "订单数据", "orders", null, new Menu[]{
                        new Menu(MENU, "用户订单查询", "user-order-check", "/user-order-check", new Menu[]{
                        }),
                }),
        };

        for (Menu folder : menus) {
            // 插入目录
            SysPermissionEntity folderEntity = folder.toEntity();
            service.save(folderEntity);
            saveRolePer(roleEntity.getId(), folderEntity.getId());

            if (folder.getChildren() == null) {
                continue;
            }
            for (Menu menu : folder.getChildren()) {
                // 插入菜单
                SysPermissionEntity menuEntity = menu.toEntity();
                menuEntity.setPid(folderEntity.getId());
                menuEntity.setOrderIndex(folder.getChildren().indexOf(menu));
                service.save(menuEntity);
                saveRolePer(roleEntity.getId(), menuEntity.getId());

                for (Menu func : menu.getChildren()) {
                    // 插入目录
                    SysPermissionEntity funcEntity = func.toEntity();
                    funcEntity.setPid(menuEntity.getId());
                    funcEntity.setOrderIndex(menu.getChildren().indexOf(func));
                    service.save(funcEntity);
                    saveRolePer(roleEntity.getId(), funcEntity.getId());
                }
            }
        }

        return ApiResponseUtil.getApiResponseSuccess();
    }

    private void saveRolePer(int roleId, int permissionId) {
        SysRolePermissionRelEntity rel =  new SysRolePermissionRelEntity();
        rel.setRoleId(roleId);
        rel.setPerId(permissionId);
        sysRolePermissionRelMapper.insert(rel);
    }

    @Data
    class Menu {
        private int id;
        private int pid;
        private String type;
        private String name;
        private String code;
        private String route;
        private int orderIndex;
        private List<Menu> children;
        Menu(String type, String name, String code, String route) {
            this(type, name, code, route, null);
        }
        Menu(String type, String name, String code, String route, Menu[] children) {
            this.type = type;
            this.name = name;
            this.code = code;
            this.route = route;
            if (children != null) {
                this.children = Arrays.asList(children);
            }
        }

        public SysPermissionEntity toEntity() {
            SysPermissionEntity entity = new SysPermissionEntity();
            entity.setCode(code);
            entity.setName(name);
            entity.setRoute(route);
            entity.setOrderIndex(orderIndex);
            entity.setType(type);
            return entity;
        }

    }
}
