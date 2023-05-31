package com.yeye.mall.order.controller;

import com.yeye.mall.order.entity.OrderEntity;
import com.yeye.mall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class RabbitController {

  @Autowired
  RabbitTemplate rabbitTemplate;


  @GetMapping("/sendMq")
  public String sendMqMsg(@RequestParam(value = "num", defaultValue = "10") Integer num) {

    for (int i = 0; i < num; i++) {
      OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
      orderReturnReasonEntity.setId(1L);
      orderReturnReasonEntity.setCreateTime(new Date());
      orderReturnReasonEntity.setName("不知道" + i);
      rabbitTemplate.convertAndSend
        ("hello-java-exchange", "hello.java", orderReturnReasonEntity);
      log.info("消息创建成功{}", orderReturnReasonEntity);
    }

    return "ok";
  }


  @GetMapping("/test/mq")
  public String mq(){
    OrderEntity orderEntity = new OrderEntity();
    orderEntity.setOrderSn("222222");
    orderEntity.setCreateTime(new Date());
    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderEntity);
    return "ok";
  }

}
