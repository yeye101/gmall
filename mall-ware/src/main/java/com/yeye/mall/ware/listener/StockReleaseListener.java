package com.yeye.mall.ware.listener;

import com.rabbitmq.client.Channel;
import com.yeye.common.to.mq.OrderTo;
import com.yeye.common.to.mq.StockLockTo;
import com.yeye.mall.ware.service.WareSkuService;
import com.yeye.mall.ware.vo.OrderVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

  @Autowired
  WareSkuService wareSkuService;

  @RabbitHandler
  private void handleStockLockedRelease(Message msg,
                                        StockLockTo content,
                                        Channel channel) {

    System.out.println("收到了消息" + content);
    try {
      wareSkuService.handleStockLockedRelease(content);
      returnAckTrue(msg, channel);
    } catch (Exception e) {
      e.printStackTrace();
      returnAckReject(msg, channel);
    }

  }

  @RabbitHandler()
  private void handleOrderCloseRelease(Message msg,
                                        OrderTo orderTo,
                                        Channel channel) {

    System.out.println("收到了消息" + orderTo);
    try {
      wareSkuService.handleOrderCloseRelease(orderTo);
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
      // 返回ack状态
      channel.basicReject(msg.getMessageProperties().getDeliveryTag(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
