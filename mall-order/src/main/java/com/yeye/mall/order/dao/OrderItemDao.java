package com.yeye.mall.order.dao;

import com.yeye.mall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
