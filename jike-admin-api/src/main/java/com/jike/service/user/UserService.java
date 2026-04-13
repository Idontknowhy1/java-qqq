package com.jike.service.user;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.mapper.user.UserMapper;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVO;
import com.jike.model.user.UserDTO;
import com.jike.service.score.UserScoreService;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.common.ConvertUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserService extends AppBaseServiceV2<UserEntity, UserVO,UserDTO> {

    final UserMapper mapper;

    public UserService(UserMapper mapper) {
        this.mapper = mapper;
    }

    public Class<UserVO> getVoClass() {
        return UserVO.class;
    }

    public Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }

    @Override
    public UserMapper getMapper() {
        return mapper;
    }

    @Autowired
    UserScoreService userScoreService;

    public UserVO getUserById(String userId)  throws Exception {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        UserEntity one = this.getOne(queryWrapper);



        if (one == null) {
            return null;
        }
        return convertTouserVo(one);
    }

    public UserVO getUserByUUID(String uuid)  throws Exception {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", uuid);
        UserEntity one = this.getOne(queryWrapper);

        if (one == null) {
            return null;
        }
        return convertTouserVo(one);
    }

    private UserVO convertTouserVo(UserEntity userEntity) {

        // 转换为VO
        UserVO userVO = ConvertUtil.convertTo(userEntity, UserVO.class);

        userVO.setId(String.valueOf(userEntity.getId()));
        // 设置是否会员
        long currentTime = System.currentTimeMillis() / 1000;
        userVO.setMaterialMember(userEntity.getMaterialMemberExpireAt() > currentTime);
        userVO.setPluginMember(userEntity.getPluginMemberExpireAt() > currentTime);
        userVO.setAiGenMember(userEntity.getAiGenMemberExpireAt() > currentTime);
        userVO.setImageSplitMember(userEntity.getImageSplitMemberExpireAt() > currentTime);

        if (userEntity.getMaterialMemberExpireAt() > 0) {
            userVO.setMaterialMemberExpireAt(DateUtil.format(new Date(userEntity.getMaterialMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
        }
        if (userEntity.getPluginMemberExpireAt() > 0) {
            userVO.setPluginMemberExpireAt(DateUtil.format(new Date(userEntity.getPluginMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
        }
        if (userEntity.getAiGenMemberExpireAt() > 0) {
            userVO.setAiGenMemberExpireAt(DateUtil.format(new Date(userEntity.getAiGenMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
        }
        if (userEntity.getImageSplitMemberExpireAt() > 0) {
            userVO.setImageSplitMemberExpireAt(DateUtil.format(new Date(userEntity.getImageSplitMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
        }

        return userVO;
    }

}
