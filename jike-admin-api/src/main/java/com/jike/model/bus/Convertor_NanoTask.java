package com.jike.model.bus;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_NanoTask {
    Convertor_NanoTask INSTANCE = Mappers.getMapper(Convertor_NanoTask.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     NanoTaskVO convertToVo(NanoTaskEntity entity);
     NanoTaskEntity convertToEntity(NanoTaskDTO dto);
     List<NanoTaskVO> convertToVoList(Collection<NanoTaskEntity> sourceList);
     PageListResult<NanoTaskVO> convertToPageResult(PageListResult<NanoTaskEntity> sourcePageResult);
}
