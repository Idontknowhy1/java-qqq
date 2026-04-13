package com.jike.service.sys;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jike.mapper.sys.SysRolePermissionRelMapper;
import com.jike.model.sys.SysRolePermissionRelEntity;
import com.jjs.common.PageListResult;
import com.jike.model.sys.Convertor_SysPermission;
import com.jike.mapper.sys.SysPermissionMapper;
import com.jike.model.sys.SysPermissionEntity;
import com.jike.model.sys.SysPermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SysPermissionService extends ServiceImpl<BaseMapper<SysPermissionEntity>, SysPermissionEntity> implements IService<SysPermissionEntity> {

    final SysPermissionMapper mapper;
    public SysPermissionService(SysPermissionMapper mapper) { this.mapper = mapper; }
    @Autowired
    SysRolePermissionRelMapper rolePermissionRelMapper;

    public PageListResult<SysPermissionVO> getPageList(Page<SysPermissionEntity> page, Wrapper<SysPermissionEntity> queryWrapper) throws Exception {
        Page<SysPermissionEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<SysPermissionEntity> list = resultPage.getRecords();
        List<SysPermissionVO> voList = Convertor_SysPermission.INSTANCE.convertToVoList(list);
        PageListResult<SysPermissionVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<SysPermissionVO> getList(Wrapper<SysPermissionEntity> queryWrapper) throws Exception {
        List<SysPermissionEntity> list = mapper.selectList(queryWrapper);
        return Convertor_SysPermission.INSTANCE.convertToVoList(list);
    }

    /**
     * 获取角色菜单
     */
    public List<SysPermissionVO> getRoleMenus(int roleId) throws Exception {
        List<SysPermissionEntity> roleMenus = mapper.getRoleMenus(roleId);
        return Convertor_SysPermission.INSTANCE.convertToVoList(roleMenus);
    }

    /**
     * 获取角色权限
     */
    public List<SysPermissionEntity> getRolePermissions(int roleId) throws Exception {
        return mapper.getRolePermissions(roleId);
    }

    /**
     * 保存用户角色
     */
    public void updateRolePermissions(int roleId, List<Integer> permissions) throws Exception {
        // 先删除
        rolePermissionRelMapper.delete(new QueryWrapper<SysRolePermissionRelEntity>().lambda().eq(SysRolePermissionRelEntity::getRoleId, roleId));

        // 再插入
        List<SysRolePermissionRelEntity> rels = new ArrayList<>();
        for (Integer permission : permissions) {
            SysRolePermissionRelEntity rel = new SysRolePermissionRelEntity();
            rel.setRoleId(roleId);
            rel.setPerId(permission);
            rels.add(rel);
        }
        rolePermissionRelMapper.insert(rels);
    }
}
