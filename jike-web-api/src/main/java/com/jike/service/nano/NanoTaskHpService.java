package com.jike.service.nano;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.nano.NanoTaskHpMapper;
import com.jike.model.nano.NanoTaskHpEntity;
import com.jike.model.nano.NanoTaskHpVO;
import com.jike.model.nano.NanoTaskHpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NanoTaskHpService extends AppBaseServiceV2<NanoTaskHpEntity, NanoTaskHpVO,NanoTaskHpDTO> {

    final NanoTaskHpMapper mapper;
    public NanoTaskHpService(NanoTaskHpMapper mapper) { this.mapper = mapper; } 
    public Class<NanoTaskHpVO> getVoClass() { return NanoTaskHpVO.class; }
    public Class<NanoTaskHpEntity> getEntityClass() { return NanoTaskHpEntity.class; } 

    @Override
    public NanoTaskHpMapper getMapper() {
        return mapper;
    }
    
}
