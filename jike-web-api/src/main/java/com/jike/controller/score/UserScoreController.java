package com.jike.controller.score;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.common.model.score.ScoreConfig;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.model.score.*;
import com.jike.redis.RedisCacheUtil;
import com.jike.request.AppRequestHeader;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.ConvertUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.score.UserScoreService;
import com.qiniu.storage.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/userscore/v1")
@RestController
@Slf4j
public class UserScoreController extends ApiBaseControllerV2<UserScoreEntity,UserScoreVO, UserScoreDTO> {

    final
    UserScoreService service;
    public UserScoreController(UserScoreService service) { this.service = service; } 
    @Override
    public UserScoreService getService() { return service; } 

    public long getDtoKey(UserScoreDTO obj) {  return obj.getId();} 
    public long getEntityKey(UserScoreEntity obj) {  return obj.getId();} 

    protected Class<UserScoreVO> getVoClass() { return UserScoreVO.class; }
    protected Class<UserScoreDTO> getDtoClass() { return UserScoreDTO.class; }
    protected Class<UserScoreEntity> getEntityClass() { return UserScoreEntity.class; }

    /**
     * 日常签到
     */
    @GetMapping("/daily-resign")
    public ApiResponse dailyResign(AppRequestHeader header) throws Exception{
        try {
            if (header.getToken().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("请登录");
            }
            if (header.getUserId().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("token无效");
            }

            String userId = header.getUserId();

            String todayResignedKey = "user:dailyresign:" + userId + ":" + DateUtil.today();
            String todayResigned = RedisCacheUtil.getString(todayResignedKey);
            if (todayResigned != null) {
                return ApiResponseUtil.getApiResponseParamsError("今日已签到，请明日再来");
            }

            ScoreConfig scoreConfig = service.getScoreConfig();
            UserScoreUpdateResult updateResult = service.update(scoreConfig.getDailySignReward(), Long.parseLong(userId), UserScoreUpdateEnum.DAILY_SIGN);
            //  Redis标记今日已签到
            RedisCacheUtil.setString(todayResignedKey, "true", 24 * 3600);

            return ApiResponseUtil.getApiResponseSuccess(updateResult.getRecord());

        }catch (Exception e){
            log.error("签到 失败，error = " + e.toString(), e);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    /**
     * 日常签到
     */
    @GetMapping("/init")
    public ApiResponse init(AppRequestHeader header) throws Exception{
        try {
            if (header.getToken().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("请登录");
            }
            if (header.getUserId().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("token无效");
            }

            String userId = header.getUserId();

            UserScoreUpdateResult updateResult = service.update(300, Long.parseLong(userId), UserScoreUpdateEnum.INIT);

            return ApiResponseUtil.getApiResponseSuccess(updateResult.getRecord());

        } catch (Exception e){
            log.error("签到 失败，error = " + e.toString(), e);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    /**
     * 获取积分配置
     */
    @GetMapping("/get-score-config")
    public ApiResponse getScoreConfig() throws Exception{
        try {
            ScoreConfig scoreConfig = service.getScoreConfig();
            return ApiResponseUtil.getApiResponseSuccess(scoreConfig);
        } catch (Exception e){
            log.error("scoreConfig 失败，error = " + e.toString(), e);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    /**
     * 查询用户积分余额
     */
    @GetMapping("/balance-info")
    @ResponseBody
    public ApiResponse getUserScore(AppRequestHeader header) throws Exception {
        try {
            if (header.getUserId().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("请登录");
            }
            UserScoreEntity one = service.getOne(new QueryWrapper<UserScoreEntity>().lambda()
                    .eq(UserScoreEntity::getUserId, header.getUserId()));

            if (one == null) {
                return ApiResponseUtil.getApiResponseSuccess(new UserScoreVO());
            }
            UserScoreVO vo = ConvertUtil.convertTo(one, UserScoreVO.class);

            String todayResignedKey = "user:dailyresign:" + header.getUserId() + ":" + DateUtil.today();

            String todayResigned = RedisCacheUtil.getString(todayResignedKey);
            vo.setTodayResigned(Boolean.parseBoolean(todayResigned));
            return ApiResponseUtil.getApiResponseSuccess(vo);

        } catch (Exception ex) {
            log.error("获取用户积分失败",ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
