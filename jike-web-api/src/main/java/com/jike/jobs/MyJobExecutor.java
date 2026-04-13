package com.jike.jobs;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.model.job.JobRecordEntity;
import com.jike.model.user.UserVipEntity;
import com.jike.model.user.UserVipVO;
import com.jike.service.job.JobRecordService;
import com.jike.service.score.UserScoreService;
import com.jike.service.user.UserVipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class MyJobExecutor {

    @Value("${app.env}")
    private String env;

    @Value("${app.job.enabled}")
    private boolean jobEnabled;

    @Autowired
    JobRecordService jobRecordService;
    @Autowired
    UserVipService userVipService;
    @Autowired
    UserScoreService userScoreService;

    /**
     * 周期分配积分
     */
    public void assignVipScore() {
        // 从tb_user_vip中查询出 last_assign_score_time < 当月01号 && expired = 0 的记录
        // for 循环
        //      根据会员级别分配积分
        //      设置 last_assign_score_time 为当前时间

        log.info("定时任务 assignVipScore 执行于: " + LocalDateTime.now());

        List<UserVipEntity> list = userVipService.list(new QueryWrapper<UserVipEntity>().lambda()
                .lt(UserVipEntity::getLastAssignScoreTime, DateUtil.beginOfDay(new Date()))
                .eq(UserVipEntity::isExpired, 0));

        for (UserVipEntity vip : list) {
            userScoreService.assignUserScore(vip);
        }

        insertJobRecord("周期分配积分");
    }

    /**
     * 每天凌晨检测用户会员到期情况
     */
    public void checkUserVipState() throws Exception {

        log.info("定时任务 checkUserVipState 执行于: " + LocalDateTime.now());

        // 从tb_user_vip表中查找过已经过期，但 expired 标记依然有效的记录
        // 将这些记录expired设置为1
        // 对应的users中的vip_level=0，material_member_expire_at=0，plugin_member_expire_at=0
        // 对应tb_user_score.vip_score = 0
        // 插入 tb_user_score_records 会员过期记录

        List<UserVipEntity> list = userVipService.list(new QueryWrapper<UserVipEntity>().lambda()
                .eq(UserVipEntity::isExpired, false)
                .lt(UserVipEntity::getExpireTime, new Date().getTime() / 1000));

        for (UserVipEntity vip : list) {
            userVipService.userVipExpired(vip);
        }

        insertJobRecord("每天凌晨检测用户会员到期情况");

    }

    // 差人一条记录
    private void insertJobRecord(String  name) {
        JobRecordEntity recordEntity = new JobRecordEntity();
        recordEntity.setName(name);
        recordEntity.setExecuteTime(new Date());
        recordEntity.setEnv(env);
        jobRecordService.save(recordEntity);
    }

}
