package com.jike.controller.score;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.jike.common.model.score.ScoreConfig;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.controller.score.request.UpdateVipScoreRequest;
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
     * 更新用户 VIP 积分（增加或减少）
     * 支持通过正负数来增加或减少用户的 VIP 积分
     */
    @PostMapping("/update-vip-score")
    public ApiResponse updateVipScore(AppRequestHeader header, @RequestBody UpdateVipScoreRequest request) throws Exception {
        try {
            // 1. 验证认证信息
            if (header.getToken().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("请登录");
            }
            if (header.getUserId().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("token 无效");
            }

            long headerUserId;
            try {
                headerUserId = Long.parseLong(header.getUserId());
            } catch (Exception e) {
                return ApiResponseUtil.getApiResponseAuthFailure("userId 无效");
            }

            // 2. 验证请求参数
            if (request == null || request.getUserId() <= 0) {
                return ApiResponseUtil.getApiResponseParamsError("userId 不能为空或无效");
            }
            if (request.getVipScoreDelta() == 0) {
                return ApiResponseUtil.getApiResponseParamsError("VIP 积分变化量不能为 0");
            }

            // 仅允许操作当前登录用户自己的积分
            if (request.getUserId() != headerUserId) {
                return ApiResponseUtil.getApiResponseParamsError("请求用户与登录用户不一致");
            }

            // 3. 调用 Service 更新积分
            UserScoreUpdateResult updateResult = service.updateVipScore(request.getUserId(), request.getVipScoreDelta());

            log.info("用户 {} 更新 VIP 积分成功，变化量：{}，新余额：{}", 
                request.getUserId(), 
                request.getVipScoreDelta(), 
                updateResult.getRecord().getVipBalanceScore());

            return ApiResponseUtil.getApiResponseSuccess(updateResult.getRecord());

        } catch (Exception e) {
            log.error("更新 VIP 积分失败，error = " + e.toString(), e);
            // 返回具体的错误信息给前端
            if (e.getMessage() != null && (
                    e.getMessage().contains("VIP 积分余额不足")
                            || e.getMessage().contains("用户不存在")
                            || e.getMessage().contains("系统繁忙")
            )) {
                return ApiResponseUtil.getApiResponseParamsError(e.getMessage());
            }
            return ApiResponseUtil.getApiResponseFailure();
        }
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
