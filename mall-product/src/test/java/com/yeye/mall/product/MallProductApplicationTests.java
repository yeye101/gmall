package com.yeye.mall.product;

import com.yeye.mall.product.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTests {


  @Autowired
  RedisUtil redisUtil;


  @Autowired
  RedissonClient redissonClient;

  @Test
  public void redisson() throws InterruptedException {
    RLock lock = redissonClient.getLock("myLock");
    lock.lock();
    Thread.sleep(30000);

    lock.unlock();
    System.out.println(lock);
  }



  @Test
  public void testStringRedisTemplate() {
    redisUtil.setEx("hello3","wowowowowowow"+UUID.randomUUID().toString(),5*60, TimeUnit.SECONDS);

    if (redisUtil.hasKey("hello3")) {

      System.out.println(redisUtil.get("hello3"));
    }
  }

  @Test
  public void contextLoads() {
  }

}
