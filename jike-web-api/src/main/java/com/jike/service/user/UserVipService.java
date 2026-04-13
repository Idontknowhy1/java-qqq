package com.jike.service.user;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.mapper.order.GoodsMapper;
import com.jike.mapper.user.UserMapper;
import com.jike.model.order.GoodsEntity;
import com.jike.model.score.UserScoreEntity;
import com.jike.model.user.*;
import com.jike.service.score.UserScoreService;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.user.UserVipMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserVipService extends AppBaseServiceV2<UserVipEntity, UserVipVO,UserVipDTO> {

    final UserVipMapper mapper;
    @Autowired
    private UserService userService;

    public UserVipService(UserVipMapper mapper) { this.mapper = mapper; }
    public Class<UserVipVO> getVoClass() { return UserVipVO.class; }
    public Class<UserVipEntity> getEntityClass() { return UserVipEntity.class; }

    @Override
    public UserVipMapper getMapper() {
        return mapper;
    }

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserVipMapper userVipMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    UserScoreService userScoreService;

    public ApiResponse addUserVip(String userId, String vipTag, UserScoreUpdateEnum scoreUpdateEnum)  throws Exception {

        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getId, userId));
        if (userEntity == null) {
            return ApiResponseUtil.getApiResponseParamsError("userId无效");
        }

        userEntity.setPluginMemberExpireAt(0);
        userEntity.setMaterialMemberExpireAt(0);
        userEntity.setAiGenMemberExpireAt(0);
        userEntity.setImageSplitMemberExpireAt(0);
        userEntity.setVipLevel(0);

        // 删除会员
        if (vipTag.equals("VIP_LV00")) {
            // 删除积分
            userScoreService.clearScore(Long.parseLong(userId));
            // 删除用户会员信息
            expireUserVip(userId,  DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "管理员删除");

            userMapper.updateById(userEntity);

            return ApiResponseUtil.getApiResponseSuccess();
        }

        // 获取VipTag对应的商品
        GoodsEntity goodsEntity = goodsMapper.selectOne(new QueryWrapper<GoodsEntity>().lambda()
                .eq(GoodsEntity::getGoodsTag, vipTag)
                        .orderBy(true ,true, GoodsEntity::getId)
                        .last("limit 1"));
        if (goodsEntity == null) {
            return ApiResponseUtil.getApiResponseParamsError("vipTag无效");
        }

        // 获取用户档期会员信息
        UserVipEntity userVipEntity = getUserVip(userId);
        if (userVipEntity != null) {
            if (userVipEntity.getVip().equals(vipTag)) {
                return ApiResponseUtil.getApiResponseParamsError("当前已拥有该会员");
            }
            if (userVipEntity.getVip().compareTo(vipTag) > 0) {
                return ApiResponseUtil.getApiResponseParamsError("您已拥有更高级别会员");
            }
        }

        String vipMemo = "";

        // 今日时间
        Date todayDate = DateUtil.parseDate(DateUtil.today());

        // 计算新会员过期时间
        Date newVipExpireDate = DateUtil.offset(todayDate, DateField.YEAR, 1);
        long newVipExpireTime = newVipExpireDate.getTime() / 1000;

        // 用户当前的VIP
        if (userVipEntity != null) {
            // 旧会员过期时间
            Date expiredDate = DateUtil.parseDate(DateUtil.format(new Date(userVipEntity.getExpireTime() * 1000), "YYYY-MM-dd"));
            // 旧会员剩余天数
            int remainDays = Math.toIntExact(DateUtil.between(todayDate, expiredDate, DateUnit.DAY));
            // 当前会员剩余天数，按当时购买价格折算剩余金额
            float remainMoney = Math.max(0, (userVipEntity.getPrice() / 365) * remainDays);
            // 新会员每天的价格
            float newVipPricePerDay = goodsEntity.getPrice() / 365;
            // 旧会员剩余天数折算成新会员天数
            int extraDays = (int) Math.floor(remainMoney / newVipPricePerDay);

            Date newVipExpireDateOffseted = DateUtil.offset(newVipExpireDate, DateField.DAY_OF_YEAR, extraDays);
            newVipExpireTime = newVipExpireDateOffseted.getTime() / 1000;

            vipMemo = "旧会员" + userVipEntity.getVip() + "(id:" + userVipEntity.getId() + ")过期时间为" + DateUtil.format(expiredDate, "YYYY-MM-dd") + "剩余" + remainDays + "天；" +
                    "当时会员价格" + userVipEntity.getPrice() + "折算到今日(" + DateUtil.today() + ")剩余" + remainMoney + "元；" +
                    "新会员价格" + goodsEntity.getPrice() + ", 旧会员剩余时间折算到新会员为" + extraDays + "天;" +
                    "购买新会员截止到" + DateUtil.format(newVipExpireDate, "YYYY-MM-dd") + "加上" + extraDays + "天为" + DateUtil.format(newVipExpireDateOffseted, "YYYY-MM-dd");

            userVipEntity.setExpired(true);
            userVipEntity.setExpiredReason("会员升级");
            userVipMapper.updateById(userVipEntity);
        }

//        https://j81r3ntapm.feishu.cn/wiki/JtM3wgQrQijzTUklrFtcGxlhnrb

        if (vipTag.equals("VIP_LV01")) {
//                简笔画素材（一年）
//                简笔画录播
//                小说授权
            userEntity.setVipLevel(1);
            userEntity.setMaterialMemberExpireAt(newVipExpireTime);

        } else if (vipTag.equals("VIP_LV02")) {
//                全部素材网使用权益（一年）
//                每月赠送300积分
//                插件
            userEntity.setVipLevel(2);
            userEntity.setPluginMemberExpireAt(newVipExpireTime);
            userEntity.setMaterialMemberExpireAt(newVipExpireTime);

            userScoreService.update(150, Long.parseLong(userId), scoreUpdateEnum, goodsEntity.getGoodsTag());

        } else if (vipTag.equals("VIP_LV03")) {
//                全部素材网使用权益（一年）
//                每月赠送900积分
//                插件
            userEntity.setVipLevel(3);
            userEntity.setPluginMemberExpireAt(newVipExpireTime);
            userEntity.setMaterialMemberExpireAt(newVipExpireTime);

            userScoreService.update(900, Long.parseLong(userId), scoreUpdateEnum, goodsEntity.getGoodsTag());
        }

        userMapper.updateById(userEntity);

        // 添加tb_user_vip表
        UserVipEntity newUserVipEntity = new UserVipEntity();
        newUserVipEntity.setUserId(Long.parseLong(userId));
        newUserVipEntity.setVip(vipTag);
        newUserVipEntity.setPrice(goodsEntity.getPrice());
        newUserVipEntity.setExpireTime(newVipExpireTime);
        newUserVipEntity.setMemo(vipMemo);
        userVipMapper.insert(newUserVipEntity);

        return ApiResponseUtil.getApiResponseSuccess();
    }

    /**
     * 获取用户当前会员信息
     */
    public UserVipEntity getUserVip(String userId) {
        return getOne(
                new QueryWrapper<UserVipEntity>().lambda()
                        .eq(UserVipEntity::getUserId, userId)
                        .eq(UserVipEntity::isExpired, false)
                        .orderBy(true, false, UserVipEntity::getId)
                        .last("limit 1")
        );
    }

    /**
     * 将用户会员设置为过期
     */
    public void expireUserVip(String userId, String reason) {
        UserVipEntity userVip = getUserVip(userId);
        if (userVip != null) {
            userVip.setExpired(true);
            userVip.setExpiredReason(reason);
            updateById(userVip);
        }
    }

    /**
     * Job: 用户会员状态检查
     */
    public void userVipExpired(UserVipEntity userVip) throws Exception {
        // 将这些记录expired设置为1
        // 对应的users中的vip_level=0，material_member_expire_at=0，plugin_member_expire_at=0
        // 对应tb_user_score.vip_score = 0
        // 插入 tb_user_score_records 会员过期记录

        userVip.setExpired(true);
        userVip.setExpireTime(new Date().getTime() / 1000);
        userVip.setExpiredReason("会员到期");
        updateById(userVip);

        userScoreService.update(0, userVip.getUserId(), UserScoreUpdateEnum.VIP_EXPIRED);

        UserEntity user = userService.getById(String.valueOf(userVip.getUserId()));
        user.setVipLevel(0);
        user.setPluginMemberExpireAt(0);
        user.setMaterialMemberExpireAt(0);
        userService.updateById(user);
    }
}
