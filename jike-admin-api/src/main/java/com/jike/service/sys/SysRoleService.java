package com.jike.service.sys;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.sys.Convertor_SysRole;
import com.jike.mapper.sys.SysRoleMapper;
import com.jike.model.sys.SysRoleEntity;
import com.jike.model.sys.SysRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SysRoleService extends ServiceImpl<BaseMapper<SysRoleEntity>, SysRoleEntity> implements IService<SysRoleEntity> {

    final SysRoleMapper mapper;
    public SysRoleService(SysRoleMapper mapper) { this.mapper = mapper; }

    public PageListResult<SysRoleVO> getPageList(Page<SysRoleEntity> page, Wrapper<SysRoleEntity> queryWrapper) throws Exception {
        Page<SysRoleEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<SysRoleEntity> list = resultPage.getRecords();
        List<SysRoleVO> voList = Convertor_SysRole.INSTANCE.convertToVoList(list);
        PageListResult<SysRoleVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<SysRoleVO> getList(Wrapper<SysRoleEntity> queryWrapper) throws Exception {
        List<SysRoleEntity> list = mapper.selectList(queryWrapper);
        return Convertor_SysRole.INSTANCE.convertToVoList(list);
    }
}
