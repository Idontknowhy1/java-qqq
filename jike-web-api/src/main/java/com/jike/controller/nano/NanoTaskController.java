package com.jike.controller.nano;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.common.model.score.ScoreConfig;
import com.jike.controller.nano.request.NanoAdminPageQuery;
import com.jike.controller.nano.request.NanoTasksDetailRequest;
import com.jike.controller.nano.request.ScriptCommitRequest;
import com.jike.mapper.user.UserMapper;
import com.jike.model.nano.*;
import com.jike.model.score.UserScoreConsumeResult;
import com.jike.model.user.UserEntity;
import com.jike.request.AppRequestHeader;
import com.jike.service.nano.NanoTaskHpService;
import com.jike.service.score.UserScoreService;
import com.jike.utils.JwtTokenUtil;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.*;
import com.jike.service.nano.NanoTaskService;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/v1/nanotask")
@RestController
@Slf4j
public class NanoTaskController extends ApiBaseControllerV2<NanoTaskEntity,NanoTaskVO, NanoTaskDTO> {

    @Autowired
    UserMapper userMapper;

    final
    NanoTaskService service;
    public NanoTaskController(NanoTaskService service) { this.service = service; }
    @Override
    public NanoTaskService getService() { return service; }

    public long getDtoKey(NanoTaskDTO obj) {  return obj.getId();}
    public long getEntityKey(NanoTaskEntity obj) {  return obj.getId();}

    protected Class<NanoTaskVO> getVoClass() { return NanoTaskVO.class; }
    protected Class<NanoTaskDTO> getDtoClass() { return NanoTaskDTO.class; }
    protected Class<NanoTaskEntity> getEntityClass() { return NanoTaskEntity.class; }

    @Autowired
    UserScoreService userScoreService;

    @Autowired
    NanoTaskHpService nanoTaskHpService;

    /**
     * 任务提交
     */
    @PostMapping("/submit")
    public ApiResponse submit(@RequestBody NanoTaskDTO taskDTO, @RequestHeader(name = "x-token", defaultValue = "") String token) throws Exception {
        try {

            String userId = JwtTokenUtil.parseToken(token);
            if (token == null || token.isEmpty() || userId == null || userId.isEmpty()) {
                return ApiResponseUtil.getApiResponseParamsError("token无效");
            }

            // 判断当前这个人已经是否是管理员
            UserEntity userEntity = userMapper.selectById(userId);
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("token无效");
            }

            NanoTaskEntity taskEntity = new NanoTaskEntity();
            taskEntity.setId(Long.parseLong(SnowflakeId.generateId()));
            taskEntity.setUserId(Long.parseLong(userId));
            taskEntity.setPrompt(taskDTO.getPrompt());
            taskEntity.setReferImages(JSON.toJSONString(taskDTO.getReferImages()));
            taskEntity.setAspectRatio(taskDTO.getAspectRatio());

            switch (taskDTO.getModelType()) {
                case "b" -> taskEntity.setModel(NanoModelEnum.BANNER.toCode());
                case "b2" -> {
                    taskEntity.setModel(NanoModelEnum.BANNER2.toCode());
                    taskEntity.setArgs("{\"res\":\"" + taskDTO.getResolution() + "\"}");
                }
                case "so2" -> taskEntity.setModel(NanoModelEnum.SORA2.toCode());
                case "so2p" -> taskEntity.setModel(NanoModelEnum.SORA2_PRO.toCode());
                case "veo3p" -> taskEntity.setModel(NanoModelEnum.VEO3_PRO.toCode());
                case "veo3pfs" -> taskEntity.setModel(NanoModelEnum.VEO3_PRO_FRAMES.toCode());
                case "veo3ffs" -> taskEntity.setModel(NanoModelEnum.VEO3_FAST_FRAMES.toCode());
                case "veo3fs" -> taskEntity.setModel(NanoModelEnum.VEO3_FRAMES.toCode());
            }

            ScoreConfig scoreConfig = userScoreService.getScoreConfig();
            UserScoreConsumeResult consumeResult = userScoreService.consume("AI绘图", scoreConfig.getAiGenPrice(), userEntity.getId());
            if (consumeResult.isSuccess()) {
                service.save(taskEntity);
                NanoTaskEntity one = service.getById(taskEntity.getId());
                return ApiResponseUtil.getApiResponseSuccess(ConvertUtil.convertTo(one, NanoTaskVO.class));
            } else {
                return ApiResponseUtil.getCoinNotEnoughtResponse();
            }

        } catch (Exception ex) {
            log.error(this.getClass().getName() + " error" + ex, ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * Hp放大
     */
    @PostMapping("/hp")
    public ApiResponse submitHp(@RequestBody NanoTaskHpDTO taskDTO, AppRequestHeader header) throws Exception {
        return reAiGenerate(taskDTO.getTaskId(), NanoModelEnum.HP.toCode(), taskDTO.getReferImages(), header);
    }

    /**
     * 多视角
     */
    @PostMapping("/multi-scene")
    public ApiResponse submitMultiScene(@RequestBody NanoTaskHpDTO taskDTO, AppRequestHeader header) throws Exception {
        return reAiGenerate(taskDTO.getTaskId(), NanoModelEnum.MULTIPLE_SCENE.toCode(), null, header);
    }

    /**
     * 再次AI生成
     */
    public ApiResponse reAiGenerate(long taskId, String model, String referImages, AppRequestHeader header) throws Exception {
        try {

            NanoTaskEntity taskEntity = service.getOne(new QueryWrapper<NanoTaskEntity>().lambda().eq(NanoTaskEntity::getId, taskId));
            if (taskEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("taskId无效");
            }
            if (taskEntity.getStatus() != 2 && !taskEntity.getResultImages().isEmpty()) {
                return ApiResponseUtil.getApiResponseParamsError("当前任务状态不可操作");
            }

            taskEntity.setId(Long.parseLong(SnowflakeId.generateId()));
            if (referImages == null || referImages.isEmpty()) {
                taskEntity.setReferImages(taskEntity.getResultImages());
            } else {
                taskEntity.setReferImages(referImages);
            }
            taskEntity.setResultImages(null);
            taskEntity.setModel(model);
            if (model == NanoModelEnum.MULTIPLE_SCENE.toCode()) {
                // 多视角不需要提示词
                taskEntity.setPrompt(null);
            }
            taskEntity.setThirdTaskId(null);
            taskEntity.setFinishTime(0);
            taskEntity.setRetryNum(0);
            taskEntity.setLogs(null);
            taskEntity.setStatus(0);
            taskEntity.setCreateTime(System.currentTimeMillis() / 1000);
            taskEntity.setUpdateTime(taskEntity.getCreateTime());

            service.save(taskEntity);
            NanoTaskEntity one = service.getById(taskEntity.getId());
            return ApiResponseUtil.getApiResponseSuccess(ConvertUtil.convertTo(one, NanoTaskVO.class));

        } catch (Exception ex) {
            log.error(this.getClass().getName() + " error" + ex, ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }


    /**
     * 再次AI生成
     */
    @PostMapping("/script-commit")
    public ApiResponse scriptCommit(@Validated @RequestBody ScriptCommitRequest request) throws Exception {
        try {

            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getUuid, request.getUuid()));
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("uuid无效");
            }

            NanoTaskEntity taskEntity = new NanoTaskEntity();
            taskEntity.setId(Long.parseLong(SnowflakeId.generateId()));
            taskEntity.setReferImages(request.getReferImages());
            taskEntity.setUserId(userEntity.getId());
            if (request.getModel().equals("ms")) {
                taskEntity.setModel(NanoModelEnum.MULTIPLE_SCENE.toCode());
            } else if (request.getModel().equals("hp")) {
                taskEntity.setModel(NanoModelEnum.HP.toCode());
            }
            service.save(taskEntity);
            return ApiResponseUtil.getApiResponseSuccess("" + taskEntity.getId());

        } catch (Exception ex) {
            log.error(this.getClass().getName() + " error" + ex, ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }



//    @GetMapping("/page-list")
//    public ApiResponse getPageList(@ModelAttribute BasePageQuery query) throws Exception {
//        try {
////            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = new QueryWrapper<NanoTaskEntity>().lambda()
////                    .orderBy(true, false, NanoTaskEntity::getCreateTime);
////            PageListResult<NanoTaskVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
////            return ApiResponseUtil.getApiResponseSuccess(result);
//
//            QueryWrapper<AdminNanoTaskVO> queryWrapper = new QueryWrapper<AdminNanoTaskVO>();
////                    .eq(true, "a.id", "1417850165762457600");
//            PageListResult<AdminNanoTaskVO> adminNanoTaskVOS = service.queryPageList(1, 20, queryWrapper);
//            return ApiResponseUtil.getApiResponseSuccess(adminNanoTaskVOS);
//
//        } catch(Exception ex){
//            log.error(this.getClass().getName() + " error" + ex,ex);
//            return ApiResponseUtil.getApiResponseFailure();
//        }
//    }

    /**
     * 获取列表
     */
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute BasePageQuery query, AppRequestHeader header) throws Exception {
        try {
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = new QueryWrapper<NanoTaskEntity>().lambda()
                    .eq(NanoTaskEntity::getUserId, header.getUserId())
                    .in(true, NanoTaskEntity::getModel, NanoModelEnum.BANNER.toCode(),
                            NanoModelEnum.BANNER2.toCode(),
                            NanoModelEnum.HP.toCode(),
                            NanoModelEnum.MULTIPLE_SCENE.toCode(),
                            NanoModelEnum.GEMINI_2_5_FLASH.toCode())
                    .orderBy(true, false, NanoTaskEntity::getCreateTime);
            PageListResult<NanoTaskVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);

            for (NanoTaskVO vo : result.getList()) {
                postNanoTaskVo(vo);
            }

            return ApiResponseUtil.getApiResponseSuccess(result);
        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取列表
     */
    @GetMapping("/admin/page-list")
    public ApiResponse getAdminPageList(@ModelAttribute NanoAdminPageQuery query) throws Exception {
        try {

            PageListResult<AdminNanoTaskVO> result = service.getPageList(query.getPageNum(), query.getPageSize(), query.getUuid());

            for (AdminNanoTaskVO vo : result.getList()) {
                postNanoTaskVo(vo);
            }

            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取列表
     */
    @PostMapping("/bat-tasks-detail")
    public ApiResponse batTaskDetail(@Validated @RequestBody NanoTasksDetailRequest request) throws Exception {
        try {
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = new QueryWrapper<NanoTaskEntity>().lambda()
                    .in(NanoTaskEntity::getId, request.getIds());
            List<NanoTaskVO> list = this.getService().getList(queryWrapper);
            List<NanoTaskVO> nanoTaskVOS = ConvertUtil.convertToList(list, NanoTaskVO.class);
            for (NanoTaskVO vo : nanoTaskVOS) {
                postNanoTaskVo(vo);
            }

            return ApiResponseUtil.getApiResponseSuccess(nanoTaskVOS);
        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }


    @Override
    public void berforeDetailResponse(NanoTaskVO vo) {
        postNanoTaskVo(vo);
    }

    private void postNanoTaskVo(NanoTaskVO vo) {
        vo.setHp(vo.getModel().equals(NanoModelEnum.HP.toCode()));
        vo.setMs(vo.getModel().equals(NanoModelEnum.MULTIPLE_SCENE.toCode()));
        vo.setModel(modelText(vo.getModel()));
        vo.setIdText("" + vo.getId());
        // createTime
        String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
        vo.setCreateTimeText(text);
        vo.setIdText("" + vo.getId());
    }

    private void postNanoTaskVo(AdminNanoTaskVO vo) {
        vo.setModel(modelText(vo.getModel()));
        vo.setIdText("" + vo.getId());
        // createTime
        String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
        vo.setCreateTimeText(text);
        vo.setIdText("" + vo.getId());
    }

    private String modelText(String model) {
        if (model.equals(NanoModelEnum.BANNER.toCode())) {
            return NanoModelEnum.BANNER.toCode2();
        } else if (model.equals(NanoModelEnum.BANNER2.toCode())) {
            return NanoModelEnum.BANNER2.toCode2();
        } else if (model.equals(NanoModelEnum.SORA2.toCode())) {
            return NanoModelEnum.SORA2.toCode2();
        } else if (model.equals(NanoModelEnum.SORA2_PRO.toCode())) {
            return NanoModelEnum.SORA2_PRO.toCode2();
        } else if (model.equals(NanoModelEnum.GEMINI_2_5_FLASH.toCode())) {
            return NanoModelEnum.GEMINI_2_5_FLASH.toCode2();
        } else if (model.equals(NanoModelEnum.MULTIPLE_SCENE.toCode())) {
            return NanoModelEnum.MULTIPLE_SCENE.toCode2();
        }
        return "";
    }
}