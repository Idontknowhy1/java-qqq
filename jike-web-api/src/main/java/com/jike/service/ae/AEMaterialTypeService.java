package com.jike.service.ae;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.ae.AEMaterialTypeMapper;
import com.jike.model.ae.AEMaterialTypeEntity;
import com.jike.model.ae.AEMaterialTypeVO;
import com.jike.model.ae.AEMaterialTypeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AEMaterialTypeService extends AppBaseServiceV2<AEMaterialTypeEntity, AEMaterialTypeVO,AEMaterialTypeDTO> {

    final AEMaterialTypeMapper mapper;
    public AEMaterialTypeService(AEMaterialTypeMapper mapper) { this.mapper = mapper; } 
    public Class<AEMaterialTypeVO> getVoClass() { return AEMaterialTypeVO.class; }
    public Class<AEMaterialTypeEntity> getEntityClass() { return AEMaterialTypeEntity.class; } 

    @Override
    public AEMaterialTypeMapper getMapper() {
        return mapper;
    }
    
}
