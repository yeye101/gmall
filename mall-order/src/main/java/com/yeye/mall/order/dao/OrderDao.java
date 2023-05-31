package com.yeye.mall.order.dao;

import com.yeye.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
