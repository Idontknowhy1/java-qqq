package com.jike.mapper.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jjs.common.AppBaseMapperV2;
import com.jike.model.order.RechargeOrderEntity;
import com.jike.model.order.RechargeOrderVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 积分充值订单 Mapper
 */
public interface RechargeOrderMapper extends AppBaseMapperV2<RechargeOrderEntity> {

    /**
     * 根据订单号查询
     */
    @Select("SELECT * FROM recharge_orders WHERE id = #{orderId}")
    RechargeOrderEntity selectByOrderId(@Param("orderId") String orderId);

    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM recharge_orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<RechargeOrderEntity> selectByUserId(@Param("userId") String userId);

    /**
     * 更新订单状态
     */
    int updateStatus(@Param("orderId") String orderId, @Param("status") String status,
                    @Param("updateTime") Long updateTime, @Param("paidTime") Long paidTime);

    /**
     * 更新二维码链接
     */
    int updateCodeUrl(@Param("orderId") String orderId, @Param("codeUrl") String codeUrl, @Param("updateTime") long updateTime);
}