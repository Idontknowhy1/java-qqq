package com.jike.service.score;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.common.model.score.ScoreConfig;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.mapper.score.UserScoreMapper;
import com.jike.mapper.score.UserScoreRecordMapper;
import com.jike.model.score.*;
import com.jike.redis.LockUtil;
import com.jike.redis.RedisCacheUtil;
import com.jjs.common.AppBaseServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserScoreService extends AppBaseServiceV2<UserScoreEntity, UserScoreVO,UserScoreDTO> {

    final UserScoreMapper mapper;
    public UserScoreService(UserScoreMapper mapper) { this.mapper = mapper; }
    public Class<UserScoreVO> getVoClass() { return UserScoreVO.class; }
    public Class<UserScoreEntity> getEntityClass() { return UserScoreEntity.class; } 

    @Override
    public UserScoreMapper getMapper() {
        return mapper;
    }

    @Autowired
    UserScoreRecordMapper userScoreRecordMapper;

    /**
     * 获取积分配置
     */
    public ScoreConfig getScoreConfig() throws Exception {
        String scoreconfig = RedisCacheUtil.getString("scoreconfig");
        ScoreConfig scoreConfig = JSON.parseObject(scoreconfig, ScoreConfig.class);
        return scoreConfig;
    }
}
