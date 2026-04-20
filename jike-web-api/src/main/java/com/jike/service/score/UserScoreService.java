package com.jike.service.score;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.common.model.score.ScoreConfig;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.mapper.score.UserScoreRecordMapper;
import com.jike.mapper.user.UserMapper;
import com.jike.mapper.user.UserVipMapper;
import com.jike.model.score.*;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVipEntity;
import com.jike.redis.LockUtil;
import com.jike.redis.RedisCacheUtil;
import com.jjs.common.AppBaseServiceV2;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.score.UserScoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserScoreService extends AppBaseServiceV2<UserScoreEntity, UserScoreVO,UserScoreDTO> {

    final UserScoreMapper mapper;
    @Autowired
    private UserMapper userMapper;

    public UserScoreService(UserScoreMapper mapper) { this.mapper = mapper; }
    public Class<UserScoreVO> getVoClass() { return UserScoreVO.class; }
    public Class<UserScoreEntity> getEntityClass() { return UserScoreEntity.class; } 

    @Override
    public UserScoreMapper getMapper() {
        return mapper;
    }

    @Autowired
    UserScoreRecordMapper userScoreRecordMapper;

    @Autowired
    UserVipMapper userVipMapper;

    /**
     * 消费
     */
    public UserScoreConsumeResult consume(String module, int score, long userId) throws Exception {
        UserScoreConsumeResult result = new UserScoreConsumeResult();

        RLock lock = LockUtil.getLock("user:score:" + userId);
        try {
            if (!lock.tryLock(30, TimeUnit.SECONDS)) {
                log.error("消费积分时获取分布式锁失败 ", new Exception("消费积分时获取分布式锁失败"));
                return null;
            }

            // 1. 获取用户积分信息
            UserScoreEntity userScoreEntity = mapper.selectOne(new QueryWrapper<UserScoreEntity>().lambda()
                    .eq(UserScoreEntity::getUserId, userId));
            if (userScoreEntity == null || userScoreEntity.getForScore() + userScoreEntity.getVipScore() < score) {
                result.setFailureReason("积分剩余不足");
                return result;
            }

            // 计算积分动态记录
            UserScoreRecordEntity userScoreRecordEntity = new UserScoreRecordEntity();
            userScoreRecordEntity.setUserId(userId);
            userScoreRecordEntity.setType(UserScoreUpdateEnum.CONSUME.getValue());
            userScoreRecordEntity.setMemo("消费" + module);

            // 2. 优先扣除会员积分
            if (userScoreEntity.getVipScore() > score) {
                // 消费了会员积分
                userScoreRecordEntity.setVipScore(score);
                // 消费了永久积分
                userScoreRecordEntity.setForScore(0);
                // 消费后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(userScoreEntity.getVipScore() - score);
                // 消费后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore());
            } else {
                // 消费了会员积分
                userScoreRecordEntity.setVipScore(userScoreEntity.getVipScore());
                // 消费了永久积分
                userScoreRecordEntity.setForScore(score - userScoreEntity.getVipScore());
                // 消费后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(0);
                // 消费后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore() - userScoreRecordEntity.getForScore());
            }

            userScoreEntity.setVipScore(userScoreRecordEntity.getVipBalanceScore());
            userScoreEntity.setForScore(userScoreRecordEntity.getForBalanceScore());
            result.setRecord(userScoreRecordEntity);

            // 保存入库
            userScoreRecordMapper.insert(userScoreRecordEntity);
            mapper.updateById(userScoreEntity);

            result.setSuccess(true);
            return result;

        } catch (Exception ex) {
            log.error("消费积分时获取分布式锁失败",ex);
            throw ex;
        } finally {
            // 确保无论如何都释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 更新积分（签到、购买、重置）
     */
    public UserScoreUpdateResult update(long score, long userId, UserScoreUpdateEnum type) throws Exception {
        return update(score, userId, type, "");
    }
    public UserScoreUpdateResult update(long score, long userId, UserScoreUpdateEnum type, String buyVipTag) throws Exception {
        UserScoreUpdateResult result = new UserScoreUpdateResult();

        RLock lock = LockUtil.getLock("user:score:" + userId);
        try {
            if (!lock.tryLock(30, TimeUnit.SECONDS)) {
                log.error("更新积分时获取分布式锁失败 ", new Exception("更新积分时获取分布式锁失败"));
                return null;
            }

            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda()
                    .eq(UserEntity::getId, userId));

            // 1. 获取用户积分信息
            UserScoreEntity userScoreEntity = mapper.selectOne(new QueryWrapper<UserScoreEntity>().lambda()
                    .eq(UserScoreEntity::getUserId, userId));
            if (userScoreEntity == null) {

                userScoreEntity = new UserScoreEntity();
                userScoreEntity.setUserId(userId);
                if (userEntity.getVipLevel() == 2) {
                    userScoreEntity.setVipScore(150);
                } else if (userEntity.getVipLevel() == 3) {
                    userScoreEntity.setVipScore(900);
                }
            }

            // 计算积分动态记录
            UserScoreRecordEntity userScoreRecordEntity = new UserScoreRecordEntity();
            userScoreRecordEntity.setUserId(userId);
            userScoreRecordEntity.setType(type.getValue());

            if (type == UserScoreUpdateEnum.DAILY_SIGN) {
                // 增加了会员积分
                userScoreRecordEntity.setVipScore(score);
                // 消费了永久积分
                userScoreRecordEntity.setForScore(0);
                // 消费后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(userScoreEntity.getVipScore() + score);
                // 消费后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore());
                userScoreRecordEntity.setMemo("每日签到");

            } else if (type == UserScoreUpdateEnum.BUY_SCORE) {
                // 增加了会员积分
                userScoreRecordEntity.setVipScore(0);
                // 消费了永久积分
                userScoreRecordEntity.setForScore(score);
                // 消费后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(userScoreEntity.getVipScore());
                // 消费后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore() + score);
                userScoreRecordEntity.setMemo("积分购买");

            } else if (type == UserScoreUpdateEnum.RESET) {

                // 上月剩余积分20%累加
                long vipScoreBalance = userScoreEntity.getVipScore();
                long balance = (long)(vipScoreBalance * 0.2) + score;

                // 变动了会员积分
                userScoreRecordEntity.setVipScore(balance);
                // 变动了永久积分
                userScoreRecordEntity.setForScore(0);
                // 变动后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(balance);
                // 变动后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore());
                userScoreRecordEntity.setMemo("积分重置，上月结余" + vipScoreBalance + ", 20%转换累加后" + balance);

            } else if (type == UserScoreUpdateEnum.BUY_VIP) {

                long vipScoreBalance = userScoreEntity.getVipScore();
                long balance = (long) vipScoreBalance + score;

                // 变动了会员积分
                userScoreRecordEntity.setVipScore(score);
                // 变动了永久积分
                userScoreRecordEntity.setForScore(0);
                // 变动后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(balance);
                // 变动后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore());

                userScoreRecordEntity.setMemo("购买" + buyVipTag + "会员，分配" + score + "积分，分配后总余额" + balance);

            } else if (type == UserScoreUpdateEnum.ADMIN_ADD_SCORE) {
                // 增加了会员积分
                userScoreRecordEntity.setVipScore(0);
                // 消费了永久积分
                userScoreRecordEntity.setForScore(score);
                // 消费后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(userScoreEntity.getVipScore());
                // 消费后剩余永久积分
                if (userScoreEntity.getForScore() + score < 0) {
                    userScoreRecordEntity.setForBalanceScore(0);
                } else {
                    userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore() + score);
                }
                userScoreRecordEntity.setMemo("管理员添加积分");

            } else if (type == UserScoreUpdateEnum.ADMIN_ADD_VIP) {
                // 增加了会员积分
                userScoreRecordEntity.setVipScore(score);
                // 消费了永久积分
                userScoreRecordEntity.setForScore(0);
                // 消费后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(userScoreEntity.getVipScore() + score);
                // 消费后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore());
                userScoreRecordEntity.setMemo("管理员添加会员");

            } else if (type == UserScoreUpdateEnum.VIP_EXPIRED) {
                // 增加了会员积分
                userScoreRecordEntity.setVipScore(0);
                // 消费了永久积分
                userScoreRecordEntity.setForScore(0);
                // 消费后剩余会员积分
                userScoreRecordEntity.setVipBalanceScore(0);
                // 消费后剩余永久积分
                userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore());
                userScoreRecordEntity.setMemo("会员过期");

            }

            userScoreEntity.setVipScore(userScoreRecordEntity.getVipBalanceScore());
            userScoreEntity.setForScore(userScoreRecordEntity.getForBalanceScore());
            result.setRecord(userScoreRecordEntity);

            // 保存入库
            userScoreRecordMapper.insert(userScoreRecordEntity);
            if (userScoreEntity.getId() > 0) {
                mapper.updateById(userScoreEntity);
            } else {
                mapper.insert(userScoreEntity);
            }

            return result;

        } catch (Exception ex) {
            log.error("更新积分时获取分布式锁失败",ex);
            throw ex;
        } finally {
            // 确保无论如何都释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    /**
     * 系统清零积分
     */
    public UserScoreUpdateResult clearScore(long userId) throws Exception {
        UserScoreUpdateResult result = new UserScoreUpdateResult();

        RLock lock = LockUtil.getLock("user:score:" + userId);
        try {
            if (!lock.tryLock(30, TimeUnit.SECONDS)) {
                log.error("更新积分时获取分布式锁失败 ", new Exception("更新积分时获取分布式锁失败"));
                return null;
            }

            // 1. 获取用户积分信息
            UserScoreEntity userScoreEntity = mapper.selectOne(new QueryWrapper<UserScoreEntity>().lambda()
                    .eq(UserScoreEntity::getUserId, userId));
            if (userScoreEntity == null) {
                userScoreEntity = new UserScoreEntity();
                userScoreEntity.setUserId(userId);
            }

            // 计算积分动态记录
            UserScoreRecordEntity userScoreRecordEntity = new UserScoreRecordEntity();
            userScoreRecordEntity.setUserId(userId);
            userScoreRecordEntity.setType(UserScoreUpdateEnum.CLEAR.getValue());

            // 增加了会员积分
            userScoreRecordEntity.setVipScore(0);
            // 消费了永久积分
            userScoreRecordEntity.setForScore(0);
            // 消费后剩余会员积分
            userScoreRecordEntity.setVipBalanceScore(0);
            // 消费后剩余永久积分
            userScoreRecordEntity.setForBalanceScore(0);
            userScoreRecordEntity.setMemo("系统清零");

            userScoreEntity.setVipScore(userScoreRecordEntity.getVipBalanceScore());
            userScoreEntity.setForScore(userScoreRecordEntity.getForBalanceScore());
            result.setRecord(userScoreRecordEntity);

            // 保存入库
            userScoreRecordMapper.insert(userScoreRecordEntity);
            if (userScoreEntity.getId() > 0) {
                mapper.updateById(userScoreEntity);
            } else {
                mapper.insert(userScoreEntity);
            }

            return result;

        } catch (Exception ex) {
            log.error("更新积分时获取分布式锁失败", ex);
            throw ex;
        } finally {
            // 确保无论如何都释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取积分配置
     */
    public ScoreConfig getScoreConfig() throws Exception {
        String scoreconfig = RedisCacheUtil.getString("scoreconfig");
        ScoreConfig scoreConfig = JSON.parseObject(scoreconfig, ScoreConfig.class);
        return scoreConfig;
    }

    /**
     * 更新用户 VIP 积分（增加或减少）
     * @param userId 用户ID
     * @param vipScoreDelta VIP积分变化量，正数增加，负数减少
     * @return 更新结果，包含操作记录
     * @throws Exception 当余额不足（减少时会导致负数）或操作失败时
     */
    public UserScoreUpdateResult updateVipScore(long userId, long vipScoreDelta) throws Exception {
        UserScoreUpdateResult result = new UserScoreUpdateResult();

        RLock lock = LockUtil.getLock("user:vip-score:" + userId);
        try {
            if (!lock.tryLock(30, TimeUnit.SECONDS)) {
                log.error("更新 VIP 积分时获取分布式锁失败", new Exception("获取分布式锁失败"));
                throw new Exception("系统繁忙，请稍后再试");
            }

            // 0. 校验用户是否存在，避免外键约束异常被吞成“系统错误”
            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda()
                    .eq(UserEntity::getId, userId));
            if (userEntity == null) {
                throw new Exception("用户不存在");
            }

            // 1. 获取用户积分信息
            UserScoreEntity userScoreEntity = mapper.selectOne(new QueryWrapper<UserScoreEntity>().lambda()
                    .eq(UserScoreEntity::getUserId, userId));
            if (userScoreEntity == null) {
                userScoreEntity = new UserScoreEntity();
                userScoreEntity.setUserId(userId);
                userScoreEntity.setVipScore(0);
                userScoreEntity.setForScore(0);
            }

            // 2. 计算新的 VIP 积分余额
            long newVipScore = userScoreEntity.getVipScore() + vipScoreDelta;

            // 3. 验证余额不为负数
            if (newVipScore < 0) {
                throw new Exception("VIP 积分余额不足，当前余额：" + userScoreEntity.getVipScore() + "，无法减少 " + Math.abs(vipScoreDelta));
            }

            // 4. 创建审计记录
            UserScoreRecordEntity userScoreRecordEntity = new UserScoreRecordEntity();
            userScoreRecordEntity.setUserId(userId);
            
            // 根据操作方向设置操作类型
            if (vipScoreDelta > 0) {
                userScoreRecordEntity.setType(UserScoreUpdateEnum.ADMIN_ADD_VIP.getValue());
                userScoreRecordEntity.setMemo("用户手动增加 VIP 积分 " + vipScoreDelta);
                userScoreRecordEntity.setVipScore(vipScoreDelta);
            } else {
                userScoreRecordEntity.setType(UserScoreUpdateEnum.CONSUME.getValue());
                userScoreRecordEntity.setMemo("用户手动减少 VIP 积分 " + Math.abs(vipScoreDelta));
                // 统一记录“消费数量”为正数，和现有消费流水语义保持一致
                userScoreRecordEntity.setVipScore(Math.abs(vipScoreDelta));
            }

            userScoreRecordEntity.setForScore(0);
            userScoreRecordEntity.setVipBalanceScore(newVipScore);
            userScoreRecordEntity.setForBalanceScore(userScoreEntity.getForScore());

            // 5. 更新用户积分记录
            userScoreEntity.setVipScore(newVipScore);
            result.setRecord(userScoreRecordEntity);

            // 6. 保存入库
            userScoreRecordMapper.insert(userScoreRecordEntity);
            if (userScoreEntity.getId() > 0) {
                mapper.updateById(userScoreEntity);
            } else {
                mapper.insert(userScoreEntity);
            }

            return result;

        } catch (Exception ex) {
            log.error("更新 VIP 积分失败，userId=" + userId + ", vipScoreDelta=" + vipScoreDelta, ex);
            throw ex;
        } finally {
            // 确保无论如何都释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * Job: 用户分配积分
     */
    public void assignUserScore(UserVipEntity userVip) {
        int score = 0;
        if (userVip.getVip().equals("VIP_LV02")) score = 150;
        else if (userVip.getVip().equals("VIP_LV03")) score = 900;
        try {
            this.update(score, userVip.getUserId(), UserScoreUpdateEnum.RESET);

            userVip.setLastAssignScoreTime(new Date().getTime() / 1000);
            userVipMapper.updateById(userVip);

        } catch (Exception ex) {
            log.error("Job: 用户分配积分 失败", ex);
        }
    }
}
