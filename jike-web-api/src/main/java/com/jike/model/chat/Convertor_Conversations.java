package com.jike.model.chat;

import com.jike.utils.TypeConverter;
import com.jjs.common.PageListResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_Conversations {
    Convertor_Conversations INSTANCE = Mappers.getMapper(Convertor_Conversations.class);
     @Mapping(
             target = "createTime",
             source = "createTime",
             qualifiedByName = "timestampToDateString"
     )
     @Mapping(
             target = "updateTime",
             source = "updateTime",
             qualifiedByName = "timestampToDateString"
     )
     ConversationsVO convertToVo(ConversationsEntity entity);

     ConversationsEntity convertToEntity(ConversationsDTO dto);
     List<ConversationsVO> convertToVoList(Collection<ConversationsEntity> sourceList);
     PageListResult<ConversationsVO> convertToPageResult(PageListResult<ConversationsEntity> sourcePageResult);
}

