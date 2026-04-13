package com.jike.service.ae;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.ae.AEMaterialMapper;
import com.jike.model.ae.AEMaterialEntity;
import com.jike.model.ae.AEMaterialVO;
import com.jike.model.ae.AEMaterialDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AEMaterialService extends AppBaseServiceV2<AEMaterialEntity, AEMaterialVO,AEMaterialDTO> {

    final AEMaterialMapper mapper;
    public AEMaterialService(AEMaterialMapper mapper) { this.mapper = mapper; } 
    public Class<AEMaterialVO> getVoClass() { return AEMaterialVO.class; }
    public Class<AEMaterialEntity> getEntityClass() { return AEMaterialEntity.class; } 

    @Override
    public AEMaterialMapper getMapper() {
        return mapper;
    }
    
}
