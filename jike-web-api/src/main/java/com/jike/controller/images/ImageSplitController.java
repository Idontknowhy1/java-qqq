package com.jike.controller.images;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.common.model.score.ScoreConfig;
import com.jike.controller.images.request.ImageSplitUpdateStateRequest;
import com.jike.model.score.UserScoreConsumeResult;
import com.jike.request.AppRequestHeader;
import com.jike.service.score.UserScoreService;
import com.jike.model.images.ImageSplitEntity;
import com.jike.model.images.ImageSplitVO;
import com.jike.model.images.ImageSplitDTO;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jike.service.images.ImageSplitService;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/imagesplit/v1")
@RestController
@Slf4j
public class ImageSplitController extends ApiBaseControllerV2<ImageSplitEntity,ImageSplitVO, ImageSplitDTO> {

    final
    ImageSplitService service;
    public ImageSplitController(ImageSplitService service) { this.service = service; } 
    @Override
    public ImageSplitService getService() { return service; } 

    public long getDtoKey(ImageSplitDTO obj) {  return obj.getId();} 
    public long getEntityKey(ImageSplitEntity obj) {  return obj.getId();} 

    protected Class<ImageSplitVO> getVoClass() { return ImageSplitVO.class; }
    protected Class<ImageSplitDTO> getDtoClass() { return ImageSplitDTO.class; }
    protected Class<ImageSplitEntity> getEntityClass() { return ImageSplitEntity.class; }

    @Autowired
    UserScoreService userScoreService;

    /**
     * 提交任务
     */
    @PostMapping("/submit")
    public ApiResponse submit(@RequestBody ImageSplitDTO dto, AppRequestHeader header) throws Exception {
        try {
            if (header.getToken().isEmpty()) {
                return ApiResponseUtil.getApiResponseParamsError("token为空");
            }
            String userId = header.getUserId();
            if (userId == null || userId.isEmpty()) {
                return ApiResponseUtil.getApiResponseParamsError("token无效");
            }

            ImageSplitEntity entity = ConvertUtil.convertTo(dto, ImageSplitEntity.class);
            entity.setUserId(Long.parseLong(userId));

            ScoreConfig scoreConfig = userScoreService.getScoreConfig();
            UserScoreConsumeResult consumeResult = userScoreService.consume("上传拆分", scoreConfig.getImageSplitPrice(), Long.parseLong(userId));
            if (consumeResult.isSuccess()) {
                service.save(entity);
                ImageSplitEntity byId = service.getById(entity.getId());
                return ApiResponseUtil.getApiResponseSuccess(ConvertUtil.convertTo(byId, ImageSplitVO.class));

            } else {
                return ApiResponseUtil.getCoinNotEnoughtResponse();
            }

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取列表
     */
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute BasePageQuery query, AppRequestHeader header) throws Exception {
        try {
            if (header.getToken().isEmpty()) {
                return ApiResponseUtil.getApiResponseParamsError("token为空");
            }
            String userId = header.getUserId();
            if (userId == null || userId.isEmpty()) {
                return ApiResponseUtil.getApiResponseParamsError("token无效");
            }

            LambdaQueryWrapper<ImageSplitEntity> queryWrapper = new QueryWrapper<ImageSplitEntity>().lambda()
                    .eq(ImageSplitEntity::getUserId, userId)
                    .orderBy(true ,false, ImageSplitEntity::getCreateTime);
            PageListResult<ImageSplitVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);

            for (ImageSplitVO vo : result.getList()) {
                String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
                vo.setCreateTimeText(text);
                if (vo.getReason() != null && !vo.getReason().isEmpty()) {
                    vo.setReason(vo.getReason().replaceAll("\n", "<br />"));
                }
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
    public ApiResponse getAllPageList(@ModelAttribute BasePageQuery query) throws Exception {
        try {
            LambdaQueryWrapper<ImageSplitEntity> queryWrapper = new QueryWrapper<ImageSplitEntity>().lambda();
            queryWrapper.orderBy(true, false, ImageSplitEntity::getCreateTime);

            PageListResult<ImageSplitVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);

            for (ImageSplitVO vo : result.getList()) {
                String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
                vo.setCreateTimeText(text);
            }

            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 修改状态
     */
    @PostMapping("/admin/update-state")
    public ApiResponse updateState(@RequestBody ImageSplitUpdateStateRequest request) throws Exception {
        try {
            LambdaUpdateWrapper<ImageSplitEntity> wrapper = new UpdateWrapper<ImageSplitEntity>().lambda()
                    .eq(ImageSplitEntity::getId, request.getId())
                    .set(ImageSplitEntity::getState, request.getState())
                    .set(request.getResultUrl() != null && !request.getResultUrl().isEmpty(),
                            ImageSplitEntity::getResultUrl,
                            request.getResultUrl())
                    .set(request.getReason() != null && !request.getReason().isEmpty(),
                            ImageSplitEntity::getReason,
                            request.getReason());

            service.update(wrapper);

            return ApiResponseUtil.getApiResponseSuccess();

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
