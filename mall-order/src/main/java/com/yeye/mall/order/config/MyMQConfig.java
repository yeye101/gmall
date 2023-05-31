package com.yeye.mall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {

  /**
   * 使用@Bean注解会自动创建binding  queue exchange
   * 一但创建，不在改动
   *
   * @return Queue
   */
  @Bean
  public Queue orderDelayQueue() {
    // (String name, boolean durable, boolean exclusive, boolean autoDelete)
    /**
     * x-dead-letter-exchange: order-event-exchange
     * x-dead-letter-routing-key: order.release.order
     * x-message-ttl: 60000
     */
    Map<String, Object> arguments = new HashMap<>();
    arguments.put("x-dead-letter-exchange", "order-event-exchange");
    arguments.put("x-dead-letter-routing-key", "order.release.order");
    arguments.put("x-message-ttl", 300000);

    // 创建死信队列
    Queue queue = new Queue
      ("order.delay.order.queue", true, false, false, arguments);
    return queue;
  }

  @Bean
  public Queue orderReleaseQueue() {
    Queue queue = new Queue
      ("order.release.order.queue", true, false, false);
    return queue;

  }

  @Bean
  public Exchange orderEventExchange() {
    //TopicExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
    TopicExchange orderEventExchange = new TopicExchange("order-event-exchange", true, false);
    return orderEventExchange;
  }

  @Bean
  public Binding orderCreateOrderBinding() {
    // (String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments)

    Binding binding = new Binding
      ("order.delay.order.queue", Binding.DestinationType.QUEUE, "order-event-exchange",
        "order.create.order", null);
    return binding;
  }

  @Bean
  public Binding orderReleaseOrderBinding() {
    Binding binding = new Binding
      ("order.release.order.queue", Binding.DestinationType.QUEUE, "order-event-exchange",
        "order.release.order", null);
    return binding;
  }


  @Bean
  public Binding orderReleaseOtherBinding() {
    Binding binding = new Binding
      ("stock.release.stock.queue", Binding.DestinationType.QUEUE, "order-event-exchange",
        "order.release.other.#", null);
    return binding;
  }

  @Bean
  public Queue orderSeckillQueue() {
    return new Queue
      ("order.seckill.order.queue", true, false, false);
  }

  @Bean
  public Binding orderSeckillBinding() {
    return new Binding
      ("order.seckill.order.queue", Binding.DestinationType.QUEUE, "order-event-exchange",
        "order.seckill.order", null);
  }


}
