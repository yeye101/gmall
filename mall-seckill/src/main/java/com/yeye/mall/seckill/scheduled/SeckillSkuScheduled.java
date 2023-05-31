package com.yeye.mall.seckill.scheduled;

import com.yeye.mall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 商品定时上架
 */
@Service
@Slf4j
public class SeckillSkuScheduled {

  @Autowired
  public SeckillService seckillService;

  @Autowired
  public RedissonClient redissonClient;

  private static final String UPLOAD_SECKILL_LOCKED = "upload:seckill:locked";


    @Scheduled(cron = "0 0 3 * * ?") // 凌晨3点执行  幂等性处理
//  @Scheduled(cron = "0 * * * * ?")
  @Async
  public void uploadSeckillSku3Days() throws InterruptedException {

    // 加锁
    RLock lock = redissonClient.getLock(UPLOAD_SECKILL_LOCKED);
    lock.lock(10, TimeUnit.SECONDS);
    try {
      // 改变状态，防止重复上架
      seckillService.uploadSeckillSku3Days();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }


    log.info("test");
    Thread.sleep(3000);
  }


}
