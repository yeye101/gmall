package com.yeye.mall.order;

import com.yeye.mall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MallOrderApplicationTests {

  @Autowired
  AmqpAdmin amqpAdmin;

  @Autowired
  RabbitTemplate rabbitTemplate;

  @Test
  public void contextLoads() {
  }

  @Test
  public void createExchange() {
    //(String name, boolean durabl~e, boolean autoDelete, Map<String, Object> arguments)
    DirectExchange directExchange = new DirectExchange
      ("hello-java-exchange", true, false);

    amqpAdmin.declareExchange(directExchange);
    log.info("hello-java-exchange 创建成功");


  }

  @Test
  public void createQueue() {
    // (String name, boolean durable, boolean exclusive, boolean autoDelete)
    Queue queue = new Queue
      ("hello-java-queue", true, false, false);
    amqpAdmin.declareQueue(queue);
    log.info("hello-java-queue 创建成功");


  }

  @Test
  public void createBinding() {
    // (String name, boolean durable, boolean exclusive, boolean autoDelete)
    Binding binding = new Binding
      ("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange",
        "hello.java", null);
    amqpAdmin.declareBinding(binding);
    log.info("hello-java-binding 创建成功");

  }


  @Test
  public void sendMsg() {
    // (String name, boolean durable, boolean exclusive, boolean autoDelete)
    OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
    orderReturnReasonEntity.setId(1L);
    orderReturnReasonEntity.setCreateTime(new Date());
    orderReturnReasonEntity.setName("不知道");
    rabbitTemplate.convertAndSend
      ("hello-java-exchange", "hello.java", orderReturnReasonEntity);
    log.info("消息创建成功{}", orderReturnReasonEntity.toString());

  }
}
