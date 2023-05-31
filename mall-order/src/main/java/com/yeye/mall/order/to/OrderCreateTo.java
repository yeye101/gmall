package com.yeye.mall.order.to;

import com.yeye.mall.order.entity.OrderEntity;
import com.yeye.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateTo {
  private OrderEntity order;

  public List<OrderItemEntity> orderItems;



}
