package com.jike.model.bus;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_Material {
    Convertor_Material INSTANCE = Mappers.getMapper(Convertor_Material.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     MaterialVO convertToVo(MaterialEntity entity);
     MaterialEntity convertToEntity(MaterialDTO dto);
     List<MaterialVO> convertToVoList(Collection<MaterialEntity> sourceList);
     PageListResult<MaterialVO> convertToPageResult(PageListResult<MaterialEntity> sourcePageResult);
}
