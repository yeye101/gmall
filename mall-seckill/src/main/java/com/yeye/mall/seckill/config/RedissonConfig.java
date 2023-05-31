package com.yeye.mall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {

  @Bean(destroyMethod = "shutdown")
  RedissonClient redisson() throws IOException {
    Config config = new Config();
    config.useSingleServer()
      .setAddress("redis://127.0.0.1:6379") //todo 写自己的redis地址
      .setPassword("zdc123456");
    return Redisson.create(config);
  }

}
