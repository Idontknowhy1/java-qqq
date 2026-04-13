package com.jike.mapper.sys;
import com.jjs.common.AppBaseMapperV2;
import com.jike.model.sys.SysPermissionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermissionMapper extends AppBaseMapperV2<SysPermissionEntity> {
    /**
     * 获取角色菜单
     */
    List<SysPermissionEntity> getRoleMenus(@Param("roleId") int roleId) throws Exception;

    /**
     * 获取角色权限
     */
    List<SysPermissionEntity> getRolePermissions(@Param("roleId") int roleId) throws Exception;
}