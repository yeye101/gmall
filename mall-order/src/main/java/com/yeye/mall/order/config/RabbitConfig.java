package com.yeye.mall.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RabbitConfig {

  @Autowired
  RabbitTemplate rabbitTemplate;

  /**
   * 消费者段反序列化
   */
  @Bean
  public MessageConverter messageConverter(ObjectMapper objectMapper) {
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  /**
   * 消费者段反序列化
   * @param connectionFactory
   * @return
   */


  /**
   * 定制RabbitTemplate
   * PostConstruct：对象创建完成后调用这个方法
   */
  @PostConstruct
  public void initRabbitTemplate() {
    rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
      /**
       * 只要到达服务ack返回为true
       * @param correlationData 当前消息唯一ID
       * @param ack 消息是否收到
       * @param cause 失败的原因
       */
      @Override
      public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("correlationData->" + correlationData
          + "ack->" + ack
          + "cause->" + cause);
      }
    });

    // 消息投递队列失败回调
    rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
      @Override
      public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息投递失败" + routingKey + "----replyText" + replyText);
      }
    });

  }

}
