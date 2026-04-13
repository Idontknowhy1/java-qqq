package com.jike.model.sys;

import com.jike.utils.TypeConverter; 
import com.jjs.common.PageListResult; 
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {TypeConverter.class})
public interface Convertor_SysAccount {
    Convertor_SysAccount INSTANCE = Mappers.getMapper(Convertor_SysAccount.class);
     //    @Mapping(
     //            target = "createTimeV2",
     //            source = "createTimeV2",
     //            qualifiedByName = "dateToString"
     //    )
     SysAccountVO convertToVo(SysAccountEntity entity);
     SysAccountEntity convertToEntity(SysAccountDTO dto);
     List<SysAccountVO> convertToVoList(Collection<SysAccountEntity> sourceList);
     PageListResult<SysAccountVO> convertToPageResult(PageListResult<SysAccountEntity> sourcePageResult);
}
