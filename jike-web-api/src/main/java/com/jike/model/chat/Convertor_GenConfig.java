package com.jike.model.chat;

import com.jike.utils.TypeConverter;
import com.jjs.common.PageListResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_GenConfig {
    Convertor_GenConfig INSTANCE = Mappers.getMapper(Convertor_GenConfig.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     GenConfigVO convertToVo(GenConfigEntity entity);
     GenConfigEntity convertToEntity(GenConfigDTO dto);
     List<GenConfigVO> convertToVoList(Collection<GenConfigEntity> sourceList);
     PageListResult<GenConfigVO> convertToPageResult(PageListResult<GenConfigEntity> sourcePageResult);
}

