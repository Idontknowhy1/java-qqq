package com.jike.model.sys;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_SysRole {
    Convertor_SysRole INSTANCE = Mappers.getMapper(Convertor_SysRole.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     SysRoleVO convertToVo(SysRoleEntity entity);
     SysRoleEntity convertToEntity(SysRoleDTO dto);
     List<SysRoleVO> convertToVoList(Collection<SysRoleEntity> sourceList);
     PageListResult<SysRoleVO> convertToPageResult(PageListResult<SysRoleEntity> sourcePageResult);
}
