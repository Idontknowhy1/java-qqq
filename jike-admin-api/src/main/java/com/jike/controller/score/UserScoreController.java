package com.jike.controller.score;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.common.model.score.ScoreConfig;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.model.score.*;
import com.jike.model.user.UserEntity;
import com.jike.redis.RedisCacheUtil;
import com.jike.request.AppRequestHeader;
import com.jike.service.score.UserScoreService;
import com.jike.service.user.UserService;
import com.jike.utils.HttpTool;
import com.jike.utils.InnerApi;
import com.jike.utils.JwtTokenUtil;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.ConvertUtil;
import com.jjs.common.HashMapUtil;
import com.jjs.common.SecurityUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;

@RequestMapping("/userscore/v1")
@RestController
@Slf4j
public class UserScoreController extends ApiBaseControllerV2<UserScoreEntity,UserScoreVO, UserScoreDTO> {

    final
    UserScoreService service;
    private final UserService userService;

    public UserScoreController(UserScoreService service, UserService userService) { this.service = service;
        this.userService = userService;
    }
    @Override
    public UserScoreService getService() { return service; } 

    public long getDtoKey(UserScoreDTO obj) {  return obj.getId();} 
    public long getEntityKey(UserScoreEntity obj) {  return obj.getId();} 

    protected Class<UserScoreVO> getVoClass() { return UserScoreVO.class; }
    protected Class<UserScoreDTO> getDtoClass() { return UserScoreDTO.class; }
    protected Class<UserScoreEntity> getEntityClass() { return UserScoreEntity.class; }

    @Autowired
    InnerApi innerApi;

    /**
     * 管理员添加积分
     */
    @GetMapping("/add-score")
    public ApiResponse addScore(@RequestParam String toUserId, @RequestParam int score) throws Exception{
        try {
            Object o = innerApi.sendPost("/inner/addUserScore", new HashMap<String, Object>() {{
                put("userId", toUserId);
                put("score", score);
            }});

            if (o instanceof ApiResponse) {
                return (ApiResponse) o;
            }

            return ApiResponseUtil.getApiResponseSuccess(o);

        } catch (Exception e){
            log.error("签到 失败，error = " + e.toString(), e);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    /**
     * 管理员添加积分
     */
    @GetMapping("/get-user-score")
    public ApiResponse getUserScore(@RequestParam String uuid) throws Exception{
        try {
            UserEntity user = userService.getOne(new QueryWrapper<UserEntity>().lambda()
                    .eq(UserEntity::getUuid, uuid));
            if (user == null) {
                return ApiResponseUtil.getApiResponseParamsError("uuid无效");
            }

            UserScoreEntity one = service.getOne(new QueryWrapper<UserScoreEntity>().lambda()
                    .eq(UserScoreEntity::getUserId, user.getId()));

            UserScoreVO vo = ConvertUtil.convertTo(one, UserScoreVO.class);
            vo.setUserIdText(String.valueOf(vo.getUserId()));
            return ApiResponseUtil.getApiResponseSuccess(vo);

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
}
