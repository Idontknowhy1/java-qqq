package com.jike.controller.order;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.controller.order.request.SearchOrderPageListRequest;
import com.jike.controller.order.request.SearchUserOrderRequest;
import com.jike.mapper.order.GoodsMapper;
import com.jike.mapper.user.UserMapper;
import com.jike.model.order.GoodsEntity;
import com.jike.model.order.OrderDTO;
import com.jike.model.order.OrderEntity;
import com.jike.model.order.OrderVO;
import com.jike.model.user.UserEntity;
import com.jike.service.order.OrderService;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.PageListResult;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RequestMapping("/order/v1")
@RestController
@Slf4j
public class OrderController extends ApiBaseControllerV2<OrderEntity,OrderVO, OrderDTO> {

    final
    OrderService service;
    public OrderController(OrderService service) { this.service = service; } 
    @Override
    public OrderService getService() { return service; } 

    public long getDtoKey(OrderDTO obj) {  return obj.getId();} 
    public long getEntityKey(OrderEntity obj) {  return obj.getId();} 

    protected Class<OrderVO> getVoClass() { return OrderVO.class; }
    protected Class<OrderDTO> getDtoClass() { return OrderDTO.class; }
    protected Class<OrderEntity> getEntityClass() { return OrderEntity.class; } 

    @Autowired
    UserMapper userMapper;
    @Autowired
    GoodsMapper goodsMapper;

    @GetMapping("/user-payed-order-list")
    @ResponseBody
    public ApiResponse queryUserOrderList(@ModelAttribute SearchUserOrderRequest request) throws Exception {
        try {
            return service.queryUserOrderList(request.getUuid(), request.getOrderNo());

        } catch (Exception ex) {
            log.error("获取用户支付订单失败，",ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 获取列表
     */
    @GetMapping("/page-list")
    public ApiResponse getPageList(@ModelAttribute SearchOrderPageListRequest query) throws Exception {
        try {
            long userId = 0;
            if (!query.getUserUUID().isEmpty()) {
                UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getUuid, query.getUserUUID()));
                if (userEntity == null) {
                    return ApiResponseUtil.getApiResponseParamsError("userUUID无效");
                }
                userId = userEntity.getId();
            }

            GoodsEntity goodsEntity = null;
            if (query.getGoodsId() > 0) {
                goodsEntity = goodsMapper.selectOne(new QueryWrapper<GoodsEntity>().lambda().eq(GoodsEntity::getId, query.getGoodsId()));
                if (goodsEntity == null) {
                    return ApiResponseUtil.getApiResponseParamsError("goodsId无效");
                }
            }

            LambdaQueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<OrderEntity>().lambda()
                    .eq(userId > 0, OrderEntity::getUserId, userId)
                    .eq(query.getStatus() > 0, OrderEntity::getStatus, query.getStatus())
                    .eq(!query.getOrderNo().isEmpty(), OrderEntity::getOrderNo, query.getOrderNo());

            if (goodsEntity != null) {
                queryWrapper.eq(OrderEntity::getGoodsId, goodsEntity.getId());
            }
            if (query.getBeginTime() > 0 && query.getEndTime() > 0) {
                queryWrapper.ge(true, OrderEntity::getCreateTime, query.getBeginTime())
                            .lt(true, OrderEntity::getCreateTime, query.getEndTime());
            }

            PageListResult<OrderVO> result = this.getService().getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
            for (OrderVO vo : result.getList()) {
                String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
                vo.setCreateTimeText(text);
            }
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
