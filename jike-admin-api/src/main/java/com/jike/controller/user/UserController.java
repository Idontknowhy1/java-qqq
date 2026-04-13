package com.jike.controller.user;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.controller.user.request.LoginRequest;
import com.jike.model.sys.SysAccountEntity;
import com.jike.model.sys.SysAccountVO;
import com.jike.service.sys.SysAccountService;
import com.jike.utils.InnerApi;
import com.jike.utils.JwtTokenUtil;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVO;
import com.jike.model.user.UserDTO;
import com.jjs.base.ApiBaseControllerV2;
import com.jike.service.user.UserService;
import com.jjs.common.ConvertUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RequestMapping("/user/v1")
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
    InnerApi innerApi;

    @Autowired
    SysAccountService sysAccountService;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ApiResponse getUserInfo() throws Exception {
        try {
            Object loginId = StpUtil.getLoginId();
            SysAccountEntity accountEntity = sysAccountService.getOne(new QueryWrapper<SysAccountEntity>().lambda()
                    .eq(SysAccountEntity::getId, loginId));
            if (accountEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("userId 无效");
            }

            SysAccountVO sysAccountVO = ConvertUtil.convertTo(accountEntity, SysAccountVO.class);
            return ApiResponseUtil.getApiResponseSuccess(sysAccountVO);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取用户会员信息
     */
    @GetMapping("/get-member-info-by-uuid")
    public ApiResponse getMemberInfoByUUId(@RequestParam(name = "uuid",defaultValue = "") String uuid) throws Exception {
        try {

            Object o = innerApi.sendGet("/inner/user/info", new HashMap<String, Object>() {{
                put("uuid", uuid);
            }});

            if (o instanceof ApiResponse) {
                return (ApiResponse) o;
            }
            return ApiResponseUtil.getApiResponseSuccess(o);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 管理员添加会员
     */
    @GetMapping("/add-vip")
    public ApiResponse addVip(@RequestParam String toUserId, @RequestParam String  vipTag) throws Exception {
        try {

            Object o = innerApi.sendPost("/inner/addUserVip", new HashMap<String, Object>() {{
                put("userId", toUserId);
                put("vipTag", vipTag);
            }});

            if (o instanceof ApiResponse) {
                return (ApiResponse) o;
            }

            return ApiResponseUtil.getApiResponseSuccess(o);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
