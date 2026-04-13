package com.jike.interceptor;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.jike.model.sys.SysPermissionEntity;
import com.jike.model.user.UserEntity;
import com.jike.redis.RedisCacheUtil;
import com.jike.service.sys.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component // 确保这是一个Spring Bean
public class StpPermissionInterceptor implements StpInterface {

    @Autowired
    SysPermissionService sysPermissionService;

    /**
     * 返回一个账号所拥有的权限码集合
     * @param loginId 用户登录ID（通常是用户id或用户名）
     * @param loginType 账号体系类型（用于多账号登录隔离）
     * @return 权限标识列表，如 ["user:add", "user:delete"]
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (StpUtil.isLogin(loginId) && StpUtil.getExtra("roleId") != null) {
            try {
                String roleId = StpUtil.getExtra("roleId").toString();

                // redis缓存读取
                String redisKey = "role:permission:" + roleId;
                List<String> codes = RedisCacheUtil.getList(redisKey);
                if (codes == null || codes.isEmpty()) {
                    // 数据库读取
                    List<SysPermissionEntity> permissions = sysPermissionService.getRolePermissions(Integer.parseInt(roleId));
                    codes = new ArrayList<>();
                    for (SysPermissionEntity permission : permissions) {
                        codes.add(permission.getCode());
                    }
                    // 填入缓存
                    RedisCacheUtil.setList(redisKey, codes);
                }
                return codes;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<String>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     * @param loginId 用户登录ID
     * @param loginType 账号体系类型
     * @return 角色标识列表，如 ["admin", "super-admin"]
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 根据loginId从数据库或缓存中查询该用户拥有的所有角色标识符
        List<String> roleList = new ArrayList<>(); // userService.getUserRoles(loginId.toString());
        return roleList;
    }
}