package com.jike.service.chat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.chat.Convertor_Conversations;
import com.jike.mapper.chat.ConversationsMapper;
import com.jike.model.chat.ConversationsEntity;
import com.jike.model.chat.ConversationsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ConversationsService extends ServiceImpl<BaseMapper<ConversationsEntity>, ConversationsEntity> implements IService<ConversationsEntity> {

    final ConversationsMapper mapper;
    public ConversationsService(ConversationsMapper mapper) { this.mapper = mapper; } 

    public PageListResult<ConversationsVO> getPageList(Page<ConversationsEntity> page, Wrapper<ConversationsEntity> queryWrapper) throws Exception {
        Page<ConversationsEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<ConversationsEntity> list = resultPage.getRecords();
        List<ConversationsVO> voList = Convertor_Conversations.INSTANCE.convertToVoList(list);
        PageListResult<ConversationsVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<ConversationsVO> getList(Wrapper<ConversationsEntity> queryWrapper) throws Exception {
        List<ConversationsEntity> list = mapper.selectList(queryWrapper);
        return Convertor_Conversations.INSTANCE.convertToVoList(list);
    }
}

