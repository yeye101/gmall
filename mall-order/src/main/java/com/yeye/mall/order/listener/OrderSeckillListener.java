package com.yeye.mall.order.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.yeye.mall.order.service.OrderService;
import com.yeye.common.to.SeckillOrderTo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = {"order.seckill.order.queue"})
@Service
public class OrderSeckillListener {

  @Autowired
  private OrderService orderService;

  @RabbitHandler
  public void handleOrderSeckill(Message msg,
                                 SeckillOrderTo content,
                                 Channel channel) {

    try {
      System.out.println("收到秒杀订单创建信息" + JSON.toJSONString(content));
      orderService.handleOrderSeckill(content);
      returnAckTrue(msg, channel);
    } catch (Exception e) {
      e.printStackTrace();
      returnAckReject(msg, channel);
    }
  }

  private void returnAckTrue(Message msg, Channel channel) {
    try {
      // 返回ack状态
      channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void returnAckReject(Message msg, Channel channel) {
    try {
      // 返回ack通道
      channel.basicReject(msg.getMessageProperties().getDeliveryTag(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
