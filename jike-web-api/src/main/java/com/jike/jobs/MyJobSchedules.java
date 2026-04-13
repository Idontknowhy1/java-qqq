package com.jike.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.job.enabled", havingValue = "true") // 当配置值为true时，该组件才生效
public class MyJobSchedules {

    @Value("${app.job.enabled}")
    private boolean jobEnabled;

    @Autowired
    MyJobExecutor jobExecutor;

    /**
     * 每月1号对所有会员发放积分（1号凌晨3点）
     */
    @Scheduled(cron = "0 0 3 1 * ? ")
    public void assignVipScore() {
        if (!jobEnabled) return;
        jobExecutor.assignVipScore();
    }

    /**
     * 每天凌晨检测用户会员到期情况(凌晨1点)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkUserVipState() throws Exception {
        if (!jobEnabled) return;
        jobExecutor.checkUserVipState();
    }

}
