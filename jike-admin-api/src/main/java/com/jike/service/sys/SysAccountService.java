package com.jike.service.sys;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.sys.Convertor_SysAccount;
import com.jike.mapper.sys.SysAccountMapper;
import com.jike.model.sys.SysAccountEntity;
import com.jike.model.sys.SysAccountVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SysAccountService extends ServiceImpl<BaseMapper<SysAccountEntity>, SysAccountEntity> implements IService<SysAccountEntity> {

    final SysAccountMapper mapper;
    public SysAccountService(SysAccountMapper mapper) { this.mapper = mapper; }

    public PageListResult<SysAccountVO> getPageList(Page<SysAccountEntity> page, Wrapper<SysAccountEntity> queryWrapper) throws Exception {
        Page<SysAccountEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<SysAccountEntity> list = resultPage.getRecords();
        List<SysAccountVO> voList = Convertor_SysAccount.INSTANCE.convertToVoList(list);
        PageListResult<SysAccountVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<SysAccountVO> getList(Wrapper<SysAccountEntity> queryWrapper) throws Exception {
        List<SysAccountEntity> list = mapper.selectList(queryWrapper);
        return Convertor_SysAccount.INSTANCE.convertToVoList(list);
    }
}
