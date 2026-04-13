package com.jike.service.notify;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.notify.WxPayNotifyMapper;
import com.jike.model.notify.WxPayNotifyEntity;
import com.jike.model.notify.WxPayNotifyVO;
import com.jike.model.notify.WxPayNotifyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WxPayNotifyService extends AppBaseServiceV2<WxPayNotifyEntity, WxPayNotifyVO,WxPayNotifyDTO> {

    final WxPayNotifyMapper mapper;
    public WxPayNotifyService(WxPayNotifyMapper mapper) { this.mapper = mapper; } 
    public Class<WxPayNotifyVO> getVoClass() { return WxPayNotifyVO.class; }
    public Class<WxPayNotifyEntity> getEntityClass() { return WxPayNotifyEntity.class; } 

    @Override
    public WxPayNotifyMapper getMapper() {
        return mapper;
    }
    
}
