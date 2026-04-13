package com.jike.service.materials;

import com.jike.mapper.materials.MaterialSquareMapper;
import com.jike.model.materials.MaterialSquareEntity;
import com.jike.model.materials.MaterialSquareVO;
import com.jike.model.materials.MaterialSquareDTO;
import com.jjs.common.AppBaseServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MaterialSquareService extends AppBaseServiceV2<MaterialSquareEntity, MaterialSquareVO,MaterialSquareDTO> {

    final MaterialSquareMapper mapper;
    public MaterialSquareService(MaterialSquareMapper mapper) { this.mapper = mapper; } 
    public Class<MaterialSquareVO> getVoClass() { return MaterialSquareVO.class; }
    public Class<MaterialSquareEntity> getEntityClass() { return MaterialSquareEntity.class; } 

    @Override
    public MaterialSquareMapper getMapper() {
        return mapper;
    }
    
}
