package com.jike.model.sys;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_SysPermission {
    Convertor_SysPermission INSTANCE = Mappers.getMapper(Convertor_SysPermission.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     SysPermissionVO convertToVo(SysPermissionEntity entity);
     SysPermissionEntity convertToEntity(SysPermissionDTO dto);
     List<SysPermissionVO> convertToVoList(Collection<SysPermissionEntity> sourceList);
     PageListResult<SysPermissionVO> convertToPageResult(PageListResult<SysPermissionEntity> sourcePageResult);
}
