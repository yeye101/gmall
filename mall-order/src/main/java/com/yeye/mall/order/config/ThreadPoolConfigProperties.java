package com.yeye.mall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties("mall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
  private Integer coreSize;
  private Integer maxSize;
  private Integer keepAliveTime;
  private TimeUnit timeUnit;

}
