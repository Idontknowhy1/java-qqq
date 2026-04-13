package com.jike.model.base;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_DictsDetail {
    Convertor_DictsDetail INSTANCE = Mappers.getMapper(Convertor_DictsDetail.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     DictsDetailVO convertToVo(DictsDetailEntity entity);
     DictsDetailEntity convertToEntity(DictsDetailDTO dto);
     List<DictsDetailVO> convertToVoList(Collection<DictsDetailEntity> sourceList);
     PageListResult<DictsDetailVO> convertToPageResult(PageListResult<DictsDetailEntity> sourcePageResult);
}
