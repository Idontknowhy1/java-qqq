package com.jike.controller.bus;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.controller.bus.request.ImageSplitUpdateStateRequest;
import com.jike.model.order.OrderVO;
import com.jjs.common.BasePageQuery;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import com.jike.model.bus.ImageSplitEntity;
import com.jike.model.bus.ImageSplitVO;
import com.jike.model.bus.ImageSplitDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.bus.ImageSplitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/imagesplit/v1")
@RestController
@Slf4j
public class ImageSplitController {

    final
    ImageSplitService service;
    public ImageSplitController(ImageSplitService service) { this.service = service; }

    /**
     * 获取列表
     */
    @SaCheckPermission("image-split")
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute BasePageQuery query) throws Exception {
        try {
            LambdaQueryWrapper<ImageSplitEntity> queryWrapper = new QueryWrapper<ImageSplitEntity>().lambda()
                    .orderBy(true, false, ImageSplitEntity::getCreateTime);
            PageListResult<ImageSplitVO> result = service.getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
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
    @SaCheckPermission("image-split")
    @PostMapping("/update-state")
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
