package com.jike.model.base;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_DictsType {
    Convertor_DictsType INSTANCE = Mappers.getMapper(Convertor_DictsType.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     DictsTypeVO convertToVo(DictsTypeEntity entity);
     DictsTypeEntity convertToEntity(DictsTypeDTO dto);
     List<DictsTypeVO> convertToVoList(Collection<DictsTypeEntity> sourceList);
     PageListResult<DictsTypeVO> convertToPageResult(PageListResult<DictsTypeEntity> sourcePageResult);
}
