package com.jike.mapper.score;

import com.jjs.common.AppBaseMapperV2;
import com.jike.model.score.PointsLedgerEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 积分账本 Mapper
 */
public interface PointsLedgerMapper extends AppBaseMapperV2<PointsLedgerEntity> {

    /**
     * 根据订单号查询账本记录
     */
    @Select("SELECT * FROM points_ledger WHERE order_id = #{orderId}")
    PointsLedgerEntity selectByOrderId(@Param("orderId") String orderId);
}