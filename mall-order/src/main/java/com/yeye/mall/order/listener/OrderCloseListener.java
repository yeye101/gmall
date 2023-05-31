package com.yeye.mall.order.listener;

import com.rabbitmq.client.Channel;
import com.yeye.mall.order.entity.OrderEntity;
import com.yeye.mall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = {"order.release.order.queue"})
@Service
public class OrderCloseListener {

  @Autowired
  private OrderService orderService;

  @RabbitHandler
  public void handleOrderClose(Message msg,
                               OrderEntity content,
                               Channel channel) {

    try {
      orderService.handleOrderClose(content);
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
