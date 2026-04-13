package com.jike.service.user;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.user.UserLoginMapper;
import com.jike.model.user.UserLoginEntity;
import com.jike.model.user.UserLoginVO;
import com.jike.model.user.UserLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLoginService extends AppBaseServiceV2<UserLoginEntity, UserLoginVO,UserLoginDTO> {

    final UserLoginMapper mapper;
    public UserLoginService(UserLoginMapper mapper) { this.mapper = mapper; } 
    public Class<UserLoginVO> getVoClass() { return UserLoginVO.class; }
    public Class<UserLoginEntity> getEntityClass() { return UserLoginEntity.class; } 

    @Override
    public UserLoginMapper getMapper() {
        return mapper;
    }
    
}
