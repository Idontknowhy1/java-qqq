package com.jike.controller.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.model.user.UserLoginEntity;
import com.jike.request.AppRequestHeader;
import com.jike.service.user.UserLoginService;
import com.jike.utils.JwtTokenUtil;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVO;
import com.jike.model.user.UserDTO;
import com.jike.vendor.WeiXinUtil;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.DefaultMethod;
import com.jike.service.user.UserService;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jjs.response.security.ResponseNoSecurity;
import com.qiniu.storage.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

@RequestMapping("/v1/user")
@RestController
@Slf4j
public class UserController extends ApiBaseControllerV2<UserEntity,UserVO, UserDTO> {

    final
    UserService service;
    public UserController(UserService service) { this.service = service; } 
    @Override
    public UserService getService() { return service; } 

    public long getDtoKey(UserDTO obj) {  return obj.getId();} 
    public long getEntityKey(UserEntity obj) {  return obj.getId();} 

    protected Class<UserVO> getVoClass() { return UserVO.class; }
    protected Class<UserDTO> getDtoClass() { return UserDTO.class; }
    protected Class<UserEntity> getEntityClass() { return UserEntity.class; } 

    @Autowired
    WeiXinUtil weiXinUtil;
    @Autowired
    UserLoginService userLoginService;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ApiResponse getUserInfo(AppRequestHeader header) throws Exception {
        try {

            // token 提取 userId
            if (header.getUserId().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("请登录");
            }

            UserVO userVO = service.getUserById(header.getUserId());
            return ApiResponseUtil.getApiResponseSuccess(userVO);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    @GetMapping("/info-k9mX7pR2")
    public ApiResponse getUserInfok9mX7pR2(@RequestParam String uuid) throws Exception {
        try {
            UserEntity one = service.getOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getUuid, uuid));
            if (one == null) {
                return ApiResponseUtil.getApiResponseParamsError("uuid无效");
            }
            return ApiResponseUtil.getApiResponseSuccess("" + one.getId());
        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取公众号登录
     */
    @PostMapping("/wx-h5-login")
    @ResponseNoSecurity
    public ApiResponse wxH5Login(@RequestBody Map map) throws Exception{
        try {

            // 1. code 兑换 openId
            // 2. 根据openId去user_login表查询userId
            // 3. 计算token并返回x-token

            // code 兑换 openId
            String code = map.get("code").toString();
            WeiXinUtil.WxCode2SessionResponse response = weiXinUtil.jsApiService.officeCode2Session(code);
            if (response == null) {
                return ApiResponseUtil.getApiResponseParamsError("获取openId失败");
            }
            if (!response.isSuccess()) {
                return  ApiResponseUtil.createApiResponse( response.getErrcode(), response.getErrmsg(), null );
//                return ApiResponseUtil.getApiResponseParamsError("获取openId失败," + response.getErrmsg());
            }

            log.info("公众号登录，响应结果：{}", JSON.toJSONString(response));

            // 根据openId去user_login表查询userId
            UserLoginEntity userLoginInfo = userLoginService.getOne(new QueryWrapper<UserLoginEntity>().lambda()
                    .eq(UserLoginEntity::getInfo, response.getOpenid()));
            if (userLoginInfo == null) {
                return ApiResponseUtil.getApiResponseParamsError("code未查询到用户信息");
            }

            // 计算token并返回x-token
            String token = JwtTokenUtil.generateToken(String.valueOf(userLoginInfo.getUserId()));
            return ApiResponseUtil.getApiResponseSuccess(new HashMap(){{
                put("x-token",token);
            }});

        }catch (Exception e){
            log.error("公众号code兑换openId 失败，error = " + e.toString(), e);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

}
