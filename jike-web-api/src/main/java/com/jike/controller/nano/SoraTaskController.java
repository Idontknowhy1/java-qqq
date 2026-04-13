package com.jike.controller.nano;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.common.model.score.ScoreConfig;
import com.jike.controller.nano.request.NanoTasksDetailRequest;
import com.jike.controller.nano.request.TaskHpDTO;
import com.jike.controller.nano.request.VideoHpTaskDTO;
import com.jike.mapper.user.UserMapper;
import com.jike.model.nano.*;
import com.jike.model.score.UserScoreConsumeResult;
import com.jike.model.user.UserEntity;
import com.jike.request.AppRequestHeader;
import com.jike.service.nano.NanoTaskHpService;
import com.jike.service.nano.NanoTaskService;
import com.jike.service.score.UserScoreService;
import com.jike.utils.HeaderAuthChecker;
import com.jike.utils.JwtTokenUtil;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jjs.common.SnowflakeId;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.qiniu.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/sorotask/v1")
@RestController
@Slf4j
public class SoraTaskController {

    @Autowired
    UserMapper userMapper;

    final
    NanoTaskService service;
    public SoraTaskController(NanoTaskService service) { this.service = service; }

    @Autowired
    UserScoreService userScoreService;
    @Autowired
    NanoTaskHpService nanoTaskHpService;
    @Autowired
    HeaderAuthChecker  authChecker;

    /**
     * 任务提交
     */
    @PostMapping("/submit")
    public ApiResponse submit(@Validated @RequestBody SoroTaskDTO taskDTO, AppRequestHeader header) throws Exception {
        try {
            ApiResponse errorResponse = authChecker.check(header);
            if (errorResponse != null) {
                return errorResponse;
            }

            // 查看当前用户有多少条尚未完成的，超过10条不准在此提交
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = listWrapper()
                    .in(true, NanoTaskEntity::getStatus,0, 1)
                    .eq(NanoTaskEntity::getUserId, header.getUserId());
            long count = service.count(queryWrapper);
            if (count > 20) {
                return ApiResponseUtil.getApiResponseParamsError("当前正在生成的任务过多，请稍后再试");
            }

//            ScoreConfig scoreConfig = userScoreService.getScoreConfig();
//            UserScoreConsumeResult consumeResult = userScoreService.consume("AI SORA("+taskDTO.getCount()+"条视频)", scoreConfig.getAiGenPrice() * taskDTO.getCount(), Long.parseLong(header.getUserId()));
//            if (consumeResult.isSuccess()) {
                for (int i = 0; i < taskDTO.getCount(); i++) {
                    NanoTaskEntity taskEntity = new NanoTaskEntity();
                    taskEntity.setId(Long.parseLong(SnowflakeId.generateId()));
                    taskEntity.setUserId(Long.parseLong(header.getUserId()));
                    taskEntity.setPrompt(taskDTO.getPrompt());
                    if (taskDTO.getReferImages() != null && !taskDTO.getReferImages().isEmpty()) {
                        taskEntity.setReferImages(taskDTO.getReferImages());
                    }
                    if (taskDTO.getModel().equals(NanoModelEnum.SORA2.toCode2())) {
                        taskEntity.setModel(NanoModelEnum.SORA2.toCode());
                    } else  if (taskDTO.getModel().equals(NanoModelEnum.SORA2_PRO.toCode2())) {
                        taskEntity.setModel(NanoModelEnum.SORA2_PRO.toCode());
                    } else  if (taskDTO.getModel().equals(NanoModelEnum.VEO3_FRAMES.toCode2())) {
                        taskEntity.setModel(NanoModelEnum.VEO3_FRAMES.toCode());
                    } else  if (taskDTO.getModel().equals(NanoModelEnum.VEO3_PRO.toCode2())) {
                        taskEntity.setModel(NanoModelEnum.VEO3_PRO.toCode());
                    } else  if (taskDTO.getModel().equals(NanoModelEnum.VEO3_PRO_FRAMES.toCode2())) {
                        taskEntity.setModel(NanoModelEnum.VEO3_PRO_FRAMES.toCode());
                    } else  if (taskDTO.getModel().equals(NanoModelEnum.VEO3_FAST_FRAMES.toCode2())) {
                        taskEntity.setModel(NanoModelEnum.VEO3_FAST_FRAMES.toCode());
                    } else  if (taskDTO.getModel().equals(NanoModelEnum.VEO3_1_FAST.toCode2())) {
                        taskEntity.setModel(NanoModelEnum.VEO3_1_FAST.toCode());
                    }

                    taskEntity.setArgs(taskDTO.getArgs());
                    service.save(taskEntity);
                }
                return ApiResponseUtil.getApiResponseSuccess();
//            } else {
//                return ApiResponseUtil.getCoinNotEnoughtResponse();
//            }

        } catch (Exception ex) {
            log.error(this.getClass().getName() + " error" + ex, ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    @PostMapping("/task/hp")
    public ApiResponse taskHP(@Validated @RequestBody TaskHpDTO taskDTO, AppRequestHeader header) throws Exception {
        try {
            ApiResponse errorResponse = authChecker.check(header);
            if (errorResponse != null) {
                return errorResponse;
            }

            // 查看当前用户有多少条尚未完成的，超过10条不准在此提交
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = listWrapper()
                    .in(true, NanoTaskEntity::getStatus,0, 1)
                    .eq(NanoTaskEntity::getUserId, header.getUserId());
            long count = service.count(queryWrapper);
            if (count > 20) {
                return ApiResponseUtil.getApiResponseParamsError("当前正在生成的任务过多，请稍后再试");
            }

            NanoTaskEntity oriTaskEntity = service.getOne(listWrapper().eq(NanoTaskEntity::getId, taskDTO.getTaskId()));
            if (oriTaskEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("taskId 无效");
            }

//            ScoreConfig scoreConfig = userScoreService.getScoreConfig();
//            UserScoreConsumeResult consumeResult = userScoreService.consume("AI SORA("+taskDTO.getCount()+"条视频)", scoreConfig.getAiGenPrice() * taskDTO.getCount(), Long.parseLong(header.getUserId()));
//            if (consumeResult.isSuccess()) {

            NanoTaskEntity taskEntity = new NanoTaskEntity();
            taskEntity.setId(Long.parseLong(SnowflakeId.generateId()));
            taskEntity.setModel(NanoModelEnum.VIDEO_HP.toCode2());
            taskEntity.setUserId(oriTaskEntity.getUserId());
            taskEntity.setPrompt(oriTaskEntity.getPrompt());
            taskEntity.setReferImages(oriTaskEntity.getReferImages());
            taskEntity.setArgs(taskDTO.getArgs());

            service.save(taskEntity);

            return ApiResponseUtil.getApiResponseSuccess();
//            } else {
//                return ApiResponseUtil.getCoinNotEnoughtResponse();
//            }

        } catch (Exception ex) {
            log.error(this.getClass().getName() + " error" + ex, ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 提交高清任务
     */
    @PostMapping("/hp/submit")
    public ApiResponse submitHp(@Validated @RequestBody VideoHpTaskDTO taskDTO, AppRequestHeader header) throws Exception {
        try {
            ApiResponse errorResponse = authChecker.check(header);
            if (errorResponse != null) {
                return errorResponse;
            }

            // 查看当前用户有多少条尚未完成的，超过10条不准在此提交
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = listWrapper()
                    .in(true, NanoTaskEntity::getStatus, 0, 1)
                    .eq(NanoTaskEntity::getUserId, header.getUserId());
            long count = service.count(queryWrapper);
            if (count > 20) {
                return ApiResponseUtil.getApiResponseParamsError("当前正在生成的任务过多，请稍后再试");
            }

//            ScoreConfig scoreConfig = userScoreService.getScoreConfig();
//            UserScoreConsumeResult consumeResult = userScoreService.consume("AI SORA("+taskDTO.getCount()+"条视频)", scoreConfig.getAiGenPrice() * taskDTO.getCount(), userEntity.getId());
//            if (consumeResult.isSuccess()) {
            NanoTaskEntity taskEntity = new NanoTaskEntity();
            taskEntity.setId(Long.parseLong(SnowflakeId.generateId()));
            taskEntity.setUserId(Long.parseLong(header.getUserId()));
            taskEntity.setModel(NanoModelEnum.VIDEO_HP.toCode());
            taskEntity.setArgs(taskDTO.getArgs());

            service.save(taskEntity);
            return ApiResponseUtil.getApiResponseSuccess();

//            } else {
//                return ApiResponseUtil.getCoinNotEnoughtResponse();
//            }

        } catch (Exception ex) {
            log.error(this.getClass().getName() + " error" + ex, ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取列表
     */
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute BasePageQuery query, AppRequestHeader header) throws Exception {
        try {
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = listWrapper()
                    .eq(NanoTaskEntity::getUserId, header.getUserId())
                    .orderBy(true, false, NanoTaskEntity::getCreateTime);
            PageListResult<NanoTaskVO> result = service.getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);

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
     * 高清任务列表
     */
    @GetMapping("/hp/page-list")
    public ApiResponse getHpPageList(@ModelAttribute BasePageQuery query, AppRequestHeader header) throws Exception {
        try {
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = new QueryWrapper<NanoTaskEntity>().lambda()
                    .eq(NanoTaskEntity::getModel, NanoModelEnum.VIDEO_HP.toCode())
                    .eq(NanoTaskEntity::getUserId, header.getUserId())
                    .orderBy(true, false, NanoTaskEntity::getCreateTime);
            PageListResult<NanoTaskVO> result = service.getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);

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
    @PostMapping("/bat-tasks-detail")
    public ApiResponse batTaskDetail(@Validated @RequestBody NanoTasksDetailRequest request) throws Exception {
        try {
            LambdaQueryWrapper<NanoTaskEntity> queryWrapper = new QueryWrapper<NanoTaskEntity>().lambda()
                    .in(NanoTaskEntity::getId, request.getIds());
            List<NanoTaskVO> list = service.getList(queryWrapper);
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

    private void postNanoTaskVo(NanoTaskVO vo) {
        vo.setIdText("" + vo.getId());
        String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
        vo.setCreateTimeText(text);
    }

    private LambdaQueryWrapper<NanoTaskEntity> listWrapper() {
        return new QueryWrapper<NanoTaskEntity>().lambda()
                .in(true, NanoTaskEntity::getModel,
                        NanoModelEnum.SORA2.toCode(),
                        NanoModelEnum.SORA2_PRO.toCode(),
                        NanoModelEnum.VEO3_PRO.toCode(),
                        NanoModelEnum.VEO3_PRO_FRAMES.toCode(),
                        NanoModelEnum.VEO3_FAST_FRAMES.toCode(),
                        NanoModelEnum.VEO3_FRAMES.toCode(),
                        NanoModelEnum.VEO3_1_FAST.toCode(),
                        NanoModelEnum.VIDEO_HP.toCode());
    }
}