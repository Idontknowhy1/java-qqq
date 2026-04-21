package com.jike.service.order;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.mapper.order.OrderMapper;
import com.jike.mapper.user.UserMapper;
import com.jike.model.order.OrderDTO;
import com.jike.model.order.OrderEntity;
import com.jike.model.order.OrderVO;
import com.jike.model.user.UserEntity;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderService extends AppBaseServiceV2<OrderEntity, OrderVO,OrderDTO> {

    final OrderMapper mapper;
    public OrderService(OrderMapper mapper) { this.mapper = mapper; } 
    public Class<OrderVO> getVoClass() { return OrderVO.class; }
    public Class<OrderEntity> getEntityClass() { return OrderEntity.class; } 

    @Override
    public OrderMapper getMapper() {
        return mapper;
    }

    @Autowired
    UserMapper userMapper;

    /**
     * 查询用户已经支付的订单信息
     */
    public ApiResponse queryUserOrderList(String uuid, String orderNo) throws Exception {

        UserEntity userEntity = null;

        if (!uuid.isEmpty()) {
            userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getUuid, uuid));
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("uuid无效");
            }
        }

        List<OrderVO> list = getList(new QueryWrapper<OrderEntity>().lambda()
                .eq(userEntity != null, OrderEntity::getUserId, userEntity == null ? 0 : userEntity.getId())
                .eq(!orderNo.isEmpty(), OrderEntity::getOrderNo, orderNo)
                .eq(OrderEntity::getStatus, 1)
                .orderBy(true, false, OrderEntity::getCreateTime));

        for (OrderVO vo : list) {
            String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
            vo.setCreateTimeText(text);
        }

        return ApiResponseUtil.getApiResponseSuccess(list);
    }
}
