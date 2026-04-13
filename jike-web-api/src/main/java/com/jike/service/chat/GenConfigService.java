package com.jike.service.chat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.chat.Convertor_GenConfig;
import com.jike.mapper.chat.GenConfigMapper;
import com.jike.model.chat.GenConfigEntity;
import com.jike.model.chat.GenConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GenConfigService extends ServiceImpl<BaseMapper<GenConfigEntity>, GenConfigEntity> implements IService<GenConfigEntity> {

    final GenConfigMapper mapper;
    public GenConfigService(GenConfigMapper mapper) { this.mapper = mapper; } 

    public PageListResult<GenConfigVO> getPageList(Page<GenConfigEntity> page, Wrapper<GenConfigEntity> queryWrapper) throws Exception {
        Page<GenConfigEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<GenConfigEntity> list = resultPage.getRecords();
        List<GenConfigVO> voList = Convertor_GenConfig.INSTANCE.convertToVoList(list);
        PageListResult<GenConfigVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<GenConfigVO> getList(Wrapper<GenConfigEntity> queryWrapper) throws Exception {
        List<GenConfigEntity> list = mapper.selectList(queryWrapper);
        return Convertor_GenConfig.INSTANCE.convertToVoList(list);
    }
}

