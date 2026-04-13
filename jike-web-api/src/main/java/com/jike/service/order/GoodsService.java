package com.jike.service.order;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.order.GoodsMapper;
import com.jike.model.order.GoodsEntity;
import com.jike.model.order.GoodsVO;
import com.jike.model.order.GoodsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoodsService extends AppBaseServiceV2<GoodsEntity, GoodsVO,GoodsDTO> {

    final GoodsMapper mapper;
    public GoodsService(GoodsMapper mapper) { this.mapper = mapper; } 
    public Class<GoodsVO> getVoClass() { return GoodsVO.class; }
    public Class<GoodsEntity> getEntityClass() { return GoodsEntity.class; } 

    @Override
    public GoodsMapper getMapper() {
        return mapper;
    }
    
}
