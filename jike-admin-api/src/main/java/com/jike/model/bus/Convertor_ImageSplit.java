package com.jike.model.bus;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_ImageSplit {
    Convertor_ImageSplit INSTANCE = Mappers.getMapper(Convertor_ImageSplit.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     ImageSplitVO convertToVo(ImageSplitEntity entity);
     ImageSplitEntity convertToEntity(ImageSplitDTO dto);
     List<ImageSplitVO> convertToVoList(Collection<ImageSplitEntity> sourceList);
     PageListResult<ImageSplitVO> convertToPageResult(PageListResult<ImageSplitEntity> sourcePageResult);
}
