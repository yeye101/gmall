package com.yeye.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.order.dao.OrderItemDao;
import com.yeye.mall.order.entity.OrderEntity;
import com.yeye.mall.order.entity.OrderItemEntity;
import com.yeye.mall.order.service.OrderItemService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<OrderItemEntity> page = this.page(
      new Query<OrderItemEntity>().getPage(params),
      new QueryWrapper<OrderItemEntity>()
    );

    return new PageUtils(page);
  }


}