package com.jike.model.bus;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_MaterialSquare {
    Convertor_MaterialSquare INSTANCE = Mappers.getMapper(Convertor_MaterialSquare.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     MaterialSquareVO convertToVo(MaterialSquareEntity entity);
     MaterialSquareEntity convertToEntity(MaterialSquareDTO dto);
     List<MaterialSquareVO> convertToVoList(Collection<MaterialSquareEntity> sourceList);
     PageListResult<MaterialSquareVO> convertToPageResult(PageListResult<MaterialSquareEntity> sourcePageResult);
}
