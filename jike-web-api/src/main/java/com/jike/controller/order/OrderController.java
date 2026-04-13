package com.jike.controller.order;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.controller.order.request.CreateOrderRequest;
import com.jike.request.AppRequestHeader;
import com.jike.utils.JwtTokenUtil;
import com.jjs.base.ApiBaseControllerV2;
import com.jike.model.order.OrderEntity;
import com.jike.model.order.OrderVO;
import com.jike.model.order.OrderDTO;
import com.jjs.common.ConvertUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


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

    @PostMapping("/create")
    @ResponseBody
    public ApiResponse createOrder(@RequestBody CreateOrderRequest request, AppRequestHeader header) {
        try {
            if (header.getToken().isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("请登录");
            }
            String userId = header.getUserId();
            if (userId == null || userId.isEmpty()) {
                return ApiResponseUtil.getApiResponseAuthFailure("token无效，请重新登录");
            }
            return service.submit(request, userId);
        } catch (Exception ex) {
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    @GetMapping("/check")
    @ResponseBody
    public ApiResponse checkOrder(@RequestParam String orderNo) {
        try {
            return service.checkOrder(orderNo);
        } catch (Exception ex) {
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    @Override
    public void berforeDetailResponse(OrderVO vo) {
        if (vo.getPayTime() > 0) {
            vo.setPayTimeText(DateUtil.format(new Date(vo.getPayTime() * 1000), "yyyy-MM-dd HH:mm:ss"));
        }
    }
}
