package com.yeye.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.order.entity.OrderEntity;
import com.yeye.common.to.SeckillOrderTo;
import com.yeye.mall.order.vo.OrderConfirmVo;
import com.yeye.mall.order.vo.OrderSubmitVo;
import com.yeye.mall.order.vo.PayAsyncVo;

import java.util.Map;

/**
 * 订单
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
public interface OrderService extends IService<OrderEntity> {

  PageUtils queryPage(Map<String, Object> params);

  // 订单确认页的数据
  OrderConfirmVo getConfirmOrder();

  R submitOrder(OrderSubmitVo vo);

  OrderEntity getOrderInfoByOrderSn(String orderSn);

  void handleOrderClose(OrderEntity content);

  String payOrder(String orderSn);

  PageUtils listWithItems(Map<String, Object> params);

  String handleOrderPayAlipaySuccessRes(PayAsyncVo asyncVo);

  void handleOrderSeckill(SeckillOrderTo content);

  R orderDetail(String orderSn);

  R reciveOrder(String orderSn);

  R cancleOrder(String orderSn);
}

