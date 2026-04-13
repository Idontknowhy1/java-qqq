package com.jike.model.base;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_Banner {
    Convertor_Banner INSTANCE = Mappers.getMapper(Convertor_Banner.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     BannerVO convertToVo(BannerEntity entity);
     BannerEntity convertToEntity(BannerDTO dto);
     List<BannerVO> convertToVoList(Collection<BannerEntity> sourceList);
     PageListResult<BannerVO> convertToPageResult(PageListResult<BannerEntity> sourcePageResult);
}
