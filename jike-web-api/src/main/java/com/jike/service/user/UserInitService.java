package com.jike.service.user;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.mapper.score.UserScoreMapper;
import com.jike.mapper.score.UserScoreRecordMapper;
import com.jike.mapper.user.UserMapper;
import com.jike.mapper.user.UserVipMapper;
import com.jike.model.score.UserScoreEntity;
import com.jike.model.score.UserScoreRecordEntity;
import com.jike.model.user.UserDTO;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVO;
import com.jike.model.user.UserVipEntity;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.common.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserInitService extends AppBaseServiceV2<UserEntity, UserVO,UserDTO> {

    final UserMapper mapper;

    @Autowired
    UserVipMapper userVipMapper;

    @Autowired
    UserScoreMapper userScoreMapper;

    @Autowired
    UserScoreRecordMapper userScoreRecordMapper;

    public UserInitService(UserMapper mapper) {
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

    public void initUserVip(UserEntity userEntity) {
        // 1. 从user表中查找出user_level>0的记录, 目前存在(0、3、6、101)
        // 2. 判断user是否存在user_vip记录，有则不处理
        //      3. user_level == 3 的都设置为2
        //      2. user_level >= 6 的都设置为3
        //      3. 判断user是否存在user_score记录，不存在则插入（LV02插入150，LV03插入900）
        //      4. 判断user是否存在user_vip(有效)记录，不存在则插入
        //      5. 设置users表的material_member_expire_at、plugin_member_expire_at一年后的今天

        boolean hasVip = userVipMapper.exists(new QueryWrapper<UserVipEntity>().lambda().eq(UserVipEntity::getUserId, userEntity.getId()));
        if (hasVip) {
            updateInitVipTag(userEntity);
            return;
        }

        int tagetVipLevel = 0;

        if (userEntity.getVipLevel() == 1) {
            tagetVipLevel = 1;
        } else if (userEntity.getVipLevel() == 3) {
            tagetVipLevel = 2;
        } else if (userEntity.getVipLevel() >= 6) {
            tagetVipLevel = 3;
        }

        boolean hasScore = userScoreMapper.exists(new QueryWrapper<UserScoreEntity>().lambda().eq(UserScoreEntity::getUserId, userEntity.getId()));
        if (!hasScore) {
            UserScoreEntity userScoreEntity = new UserScoreEntity();
            userScoreEntity.setUserId(userEntity.getId());
            if (tagetVipLevel == 3) {
                userScoreEntity.setVipScore(900);
            } else if (tagetVipLevel == 2) {
                userScoreEntity.setVipScore(150);
            }
            userScoreMapper.insert(userScoreEntity);

            UserScoreRecordEntity userScoreRecordEntity = new UserScoreRecordEntity();
            userScoreRecordEntity.setUserId(userEntity.getId());
            userScoreRecordEntity.setVipScore(userScoreEntity.getVipScore());
            userScoreRecordEntity.setMemo("VIP初始化");
            userScoreRecordEntity.setVipBalanceScore(userScoreEntity.getVipScore());
            userScoreRecordEntity.setType("INIT_VIP");
            userScoreRecordMapper.insert(userScoreRecordEntity);
        }

        UserVipEntity userVipEntity = new UserVipEntity();
        userVipEntity.setUserId(userEntity.getId());
        if (tagetVipLevel == 3) {
            userVipEntity.setVip("VIP_LV03");
        } else if (tagetVipLevel == 2) {
            userVipEntity.setVip("VIP_LV02");
        } else if (tagetVipLevel == 1) {
            userVipEntity.setVip("VIP_LV01");
        }

//        if (userVipEntity.getVip() == null || userVipEntity.getVip().equals("")) {
//            System.out.println("");
//        }

        userVipMapper.insert(userVipEntity);

        // 计算新会员过期时间
        Date todayDate = DateUtil.parseDate(DateUtil.today());
        Date newVipExpireDate = DateUtil.offset(todayDate, DateField.YEAR, 1);
        long newVipExpireTime = newVipExpireDate.getTime() / 1000;
//        userEntity.setVipLevel(tagetVipLevel);
        userEntity.setMaterialMemberExpireAt(newVipExpireTime);
        userEntity.setPluginMemberExpireAt(newVipExpireTime);

        updateInitVipTag(userEntity);
    }

    private void updateInitVipTag(UserEntity userEntity) {
        userEntity.setInitedVip(true);
        mapper.updateById(userEntity);
    }

//    public static void main(String[] args) {
//        Date todayDate = DateUtil.parseDate(DateUtil.today());
//        Date newVipExpireDate = DateUtil.offset(todayDate, DateField.YEAR, 1);
//        long newVipExpireTime = newVipExpireDate.getTime() / 1000;
//        System.out.println(newVipExpireTime);
//    }

}
