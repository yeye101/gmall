package com.yeye.mall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class testSchedule {

  /**
   * @EnableAsync
   * @EnableScheduling // 开启定时任务
   *
   * Application加上注解
   *  @Scheduled(cron = "* * * * * ?")
   *   @Async
   * 默认是阻塞逻辑
   *  1.异步运行线程池
   *  2.定时任务线程池；设置pool
   */
//  @Scheduled(cron = "* * * * * ?")
//  @Async
  public void test() throws InterruptedException {
    log.info("test");
    Thread.sleep(3000);
  }

}
