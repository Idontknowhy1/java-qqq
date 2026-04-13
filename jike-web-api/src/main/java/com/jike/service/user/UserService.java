package com.jike.service.user;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.mapper.user.UserMapper;
import com.jike.mapper.user.UserVipMapper;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVO;
import com.jike.model.user.UserDTO;
import com.jike.model.user.UserVipEntity;
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

    @Autowired
    UserVipMapper userVipMapper;

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

    public UserVO getUserById(String userId)  throws Exception {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        UserEntity one = this.getOne(queryWrapper);

        if (one == null) {
            return null;
        }

        UserVO userVO = convertTouserVo(one);

        UserVipEntity userVipEntity = userVipMapper.selectOne(new QueryWrapper<UserVipEntity>().lambda()
                .eq(UserVipEntity::getUserId, one.getId())
                .eq(UserVipEntity::isExpired, false));

        userVO.setVipLevel(0);
        userVO.setMaterialMemberExpireAt("");
        userVO.setPluginMemberExpireAt("");
        userVO.setMaterialMember(false);
        userVO.setPluginMember(false);

        if (userVipEntity != null) {
            switch (userVipEntity.getVip()) {
                case "VIP_LV01" -> userVO.setVipLevel(1);
                case "VIP_LV02" -> userVO.setVipLevel(2);
                case "VIP_LV03" -> userVO.setVipLevel(3);
            }

            userVO.setVipExpireAt(DateUtil.format(new Date(userVipEntity.getExpireTime() * 1000), "yyyy-MM-dd"));
            userVO.setMaterialMemberExpireAt(userVO.getVipExpireAt());
            userVO.setMaterialMember(true);

            // PluginOnly 为LV01开后门的
            if (userVO.getVipLevel() >= 2 || userVipEntity.isPluginOnly()) {
                userVO.setPluginMemberExpireAt(userVO.getVipExpireAt());
                userVO.setPluginMember(true);
            }
        }

        return userVO;
    }

    private UserVO convertTouserVo(UserEntity userEntity) {

        // 转换为VO
        UserVO userVO = ConvertUtil.convertTo(userEntity, UserVO.class);

        userVO.setId(String.valueOf(userEntity.getId()));
        // 设置是否会员
        long currentTime = System.currentTimeMillis() / 1000;
        userVO.setMaterialMember(userEntity.getMaterialMemberExpireAt() > currentTime);
        userVO.setPluginMember(userEntity.getPluginMemberExpireAt() > currentTime);
//        userVO.setAiGenMember(userEntity.getAiGenMemberExpireAt() > currentTime);
//        userVO.setImageSplitMember(userEntity.getImageSplitMemberExpireAt() > currentTime);

        if (userEntity.getMaterialMemberExpireAt() > 0) {
            userVO.setMaterialMemberExpireAt(DateUtil.format(new Date(userEntity.getMaterialMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
        }
        if (userEntity.getPluginMemberExpireAt() > 0) {
            userVO.setPluginMemberExpireAt(DateUtil.format(new Date(userEntity.getPluginMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
        }
//        if (userEntity.getAiGenMemberExpireAt() > 0) {
//            userVO.setAiGenMemberExpireAt(DateUtil.format(new Date(userEntity.getAiGenMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
//        }
//        if (userEntity.getImageSplitMemberExpireAt() > 0) {
//            userVO.setImageSplitMemberExpireAt(DateUtil.format(new Date(userEntity.getImageSplitMemberExpireAt() * 1000), "yyyy-MM-dd HH:mm:ss"));
//        }

        return userVO;
    }

}
