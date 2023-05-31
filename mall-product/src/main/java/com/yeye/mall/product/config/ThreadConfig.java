package com.yeye.mall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadConfig {

  @Bean
  public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties) {
    return new ThreadPoolExecutor(properties.getCoreSize(),
      properties.getMaxSize(),
      properties.getKeepAliveTime(),
      properties.getTimeUnit(),
      new LinkedBlockingDeque<>(100000),
      Executors.defaultThreadFactory(),
      new ThreadPoolExecutor.AbortPolicy());

  }


}
