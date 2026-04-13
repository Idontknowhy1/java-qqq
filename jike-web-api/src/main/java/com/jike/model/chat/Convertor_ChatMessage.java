package com.jike.model.chat;

import com.jike.service.chat.ChatMessageService;
import com.jike.utils.TypeConverter;
import com.jjs.common.PageListResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_ChatMessage {
    Convertor_ChatMessage INSTANCE = Mappers.getMapper(Convertor_ChatMessage.class);

    //    @Mapping(
    //            target = "createTimeV2",
    //            source = "createTimeV2",
    //            qualifiedByName = "dateToString"
    //    )
    ChatMessageVO convertToVo(ChatMessageEntity entity);

    ChatMessageService.ChatMsg convertToMsg(ChatMessageEntity entity);

    List<ChatMessageService.ChatMsg> convertToMsgList(List<ChatMessageEntity> list);

    ChatMessageEntity convertToEntity(ChatMessageDTO dto);

    List<ChatMessageVO> convertToVoList(Collection<ChatMessageEntity> sourceList);

    PageListResult<ChatMessageVO> convertToPageResult(PageListResult<ChatMessageEntity> sourcePageResult);
}

