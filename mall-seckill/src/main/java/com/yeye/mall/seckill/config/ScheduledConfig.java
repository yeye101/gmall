package com.yeye.mall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling // 开启定时任务
@Configuration
public class ScheduledConfig {
}
