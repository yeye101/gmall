package com.yeye.mall.ware.config;

import com.rabbitmq.client.Channel;
import com.yeye.common.to.mq.StockLockTo;
import com.yeye.mall.ware.entity.WareOrderTaskEntity;
import com.yeye.mall.ware.vo.WareSkuStock;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
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
  public Queue stockDelayStockQueue() {
    // (String name, boolean durable, boolean exclusive, boolean autoDelete)
    /**
     * x-dead-letter-exchange: order-event-exchange
     * x-dead-letter-routing-key: order.release.order
     * x-message-ttl: 60000
     */
    Map<String, Object> arguments = new HashMap<>();
    arguments.put("x-dead-letter-exchange", "stock-event-exchange");
    arguments.put("x-dead-letter-routing-key", "stock.release.stock");
    // 50min的死信队列
    arguments.put("x-message-ttl", 600000);

    // 创建死信队列
    return new Queue
      ("stock.delay.stock.queue", true, false, false, arguments);
  }

  @Bean
  public Queue stockReleaseStockQueue() {
    return new Queue("stock.release.stock.queue", true, false, false);

  }

  @Bean
  public Exchange stockEventExchange() {
    return new TopicExchange("stock-event-exchange", true, false);

  }

  @Bean
  public Binding stockLockedBinding() {
    return new Binding
      ("stock.delay.stock.queue", Binding.DestinationType.QUEUE, "stock-event-exchange",
        "stock.locked", null);
  }

  @Bean
  public Binding stockReleaseBinding() {
    return new Binding
      ("stock.release.stock.queue", Binding.DestinationType.QUEUE, "stock-event-exchange",
        "stock.release.#", null);
  }

}
