package com.yeye.mall.seckill.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeye.common.to.MemberRes;
import com.yeye.common.to.SeckillOrderTo;
import com.yeye.common.utils.R;
import com.yeye.mall.seckill.feign.CouponFeignService;
import com.yeye.mall.seckill.feign.ProductFeignService;
import com.yeye.mall.seckill.interceptor.LoginUserInterceptor;
import com.yeye.mall.seckill.service.SeckillService;
import com.yeye.mall.seckill.to.SeckillSkuRedisTo;
import com.yeye.mall.seckill.util.RedisUtil;
import com.yeye.mall.seckill.vo.SeckillSessionWithSkuVo;
import com.yeye.mall.seckill.vo.SeckillSkuRelationVo;
import com.yeye.mall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

  private static final String SESSION_CACHE_PREFIX = "seckill:session:";

  private static final String SKUS_CACHE_PREFIX = "seckill:skus";

  private static final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; //加随机码

  @Autowired
  private CouponFeignService couponFeignService;

  @Autowired
  private ProductFeignService productFeignService;

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private RedissonClient redissonClient;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  public void uploadSeckillSku3Days() {


    R sessionRes = couponFeignService.getLates3DaysSession();
    if (sessionRes.getCode() != 0) {
      log.error("远程方法调用错误--[getLates3DaysSession]");
    }
    List<SeckillSessionWithSkuVo> data = sessionRes.getData("data", new TypeReference<List<SeckillSessionWithSkuVo>>() {
    });
    // 1.保存活动信息|
    saveSeckillSessionInfos(data);
    // 2.保存活动商品信息
    saveSeckillSessionSkuInfos(data);

  }

  // 返回当前可以秒杀的商品信息
  @Override
  public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
    List<SeckillSkuRedisTo> data = new ArrayList<>();

    // 1.确定场次
    Long time = new Date().getTime();
    Set<String> keys = redisUtil.keys(SESSION_CACHE_PREFIX + "*");
    // seckill:session:1680393600000_1680429600000
    for (String key : keys) {
      String replace = key.replace(SESSION_CACHE_PREFIX, "");
      String[] s = replace.split("_");
      Long startTime = Long.valueOf(s[0]);
      Long endTime = Long.valueOf(s[1]);

      if (time <= startTime || time >= endTime) {
        // 不能删除，统一处理
        break;
      }
      // 查出所有的key
      List<String> keyList = redisUtil.lRange(key, -100, 100);
      StringRedisTemplate redisTemplate = redisUtil.getStringRedisTemplate();
      BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUS_CACHE_PREFIX);
      List<String> skuItems = ops.multiGet(keyList);

      if (CollectionUtils.isEmpty(skuItems)) {
        break;
      }
      skuItems.forEach(item -> {
        SeckillSkuRedisTo redisTo = JSON.parseObject(item.toString(), SeckillSkuRedisTo.class);
        data.add(redisTo);
      });


    }


    return data;
  }

  @Override
  public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
    SeckillSkuRedisTo data = new SeckillSkuRedisTo();
    StringRedisTemplate redisTemplate = redisUtil.getStringRedisTemplate();
    BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUS_CACHE_PREFIX);

    Set<String> keys = ops.keys();
    if (CollectionUtils.isEmpty(keys)) {
      return data;
    }
    String reg = "\\d_" + skuId;
    for (String key : keys) {
      if (Pattern.matches(reg, key)) {
        String s = ops.get(key);
        data = JSON.parseObject(s, SeckillSkuRedisTo.class);
        break;
      }
    }
    if (Objects.isNull(data.getId())){
      return data;
    }


    Long startTime = data.getStartTime();
    Long endTime = data.getEndTime();
    // 处理随机码
    Long time = new Date().getTime();
    if (time <= startTime || time >= endTime) {
      data.setRandomCode(null);
    }

    return data;
  }

  @Override
  public String seckill(String id, String randomCode, Integer num) {

    MemberRes memberRes = LoginUserInterceptor.loginUser.get();

    StringRedisTemplate redisTemplate = redisUtil.getStringRedisTemplate();
    BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUS_CACHE_PREFIX);
    String seckillSku = ops.get(id);
    if (StringUtils.isEmpty(seckillSku)) {
      return null;
    }
    SeckillSkuRedisTo data = JSON.parseObject(seckillSku, SeckillSkuRedisTo.class);

    Long startTime = data.getStartTime();
    Long endTime = data.getEndTime();
    // 1.判断时间
    Long now = new Date().getTime();
    if (now <= startTime || now >= endTime) {
      return null;
    }

    // 2.校验随机码
    String token = data.getRandomCode();
    String killId = data.getPromotionSessionId() + "_" + data.getSkuId();
    if (!StringUtils.equals(token, randomCode) || !StringUtils.equals(killId, id)) {
      return null;
    }

    // 3.购买数量验证
    if (data.getSeckillLimit().intValue() < num) {
      return null;
    }

    // 4.验证是否买过 秒杀成功就标识userId_SessionId_skuId作为key作为购买痕迹
    String userKey = killId + "_" + memberRes.getId();
    Long expireTime = endTime - now;
    boolean bool = redisUtil.setIfAbsent(userKey, num.toString(), expireTime, TimeUnit.MILLISECONDS);
    if (!bool) {
      return null;
    }
    // 5.占位成功，则可以购买 ,同时占位
    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
    try {
      boolean tryAcquire = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
      if (tryAcquire) {
        // 获取订单号（雪花算法）
        String orderSn = IdUtil.getSnowflakeNextIdStr();
        SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
        BeanUtils.copyProperties(data, seckillOrderTo);

        seckillOrderTo.setOrderSn(orderSn);
        seckillOrderTo.setMemberId(memberRes.getId());
        seckillOrderTo.setNum(num);
        rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);
        return orderSn;
      } else {
        return null;
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void saveSeckillSessionInfos(List<SeckillSessionWithSkuVo> data) {

    data.forEach(item -> {
      Long startTime = item.getStartTime().getTime();
      Long endTime = item.getEndTime().getTime();
      String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
      List<String> sessionIdAndskuIds = item.getList().stream()
        .map(k -> item.getId().toString() + "_" + k.getSkuId().toString())
        .collect(Collectors.toList());
      // 如果已经有key，直接返回
      Boolean hasKey = redisUtil.hasKey(key);
      if (hasKey) {
        return;
      }
      redisUtil.lLeftPushAll(key, sessionIdAndskuIds);

    });

  }

  private void saveSeckillSessionSkuInfos(List<SeckillSessionWithSkuVo> data) {
    // 查出所有的sku信息
    List<Long> skuIds = new ArrayList<>();
    for (SeckillSessionWithSkuVo session : data) {
      List<Long> ids = session.getList().stream().map(SeckillSkuRelationVo::getSkuId).distinct().collect(Collectors.toList());
      skuIds.addAll(ids);
    }
    skuIds = skuIds.stream().distinct().collect(Collectors.toList());
    if (CollectionUtils.isEmpty(skuIds)) {
      log.info("skuIds为空");
      return;
    }
    R skuInfoRes = productFeignService.getSkuInfoByIds(skuIds);
    if (skuInfoRes.getCode() != 0) {
      log.error("远程方法调用错误--[productFeignService:getSkuInfoByIds]");
    }
    List<SkuInfoVo> skuInfoVoList = skuInfoRes.getData("data", new TypeReference<List<SkuInfoVo>>() {
    });
    Map<Long, SkuInfoVo> skuInfoVoMap = skuInfoVoList.stream().collect(Collectors.toMap(SkuInfoVo::getSkuId, v -> v));

    data.forEach(item -> {
      StringRedisTemplate redisTemplate = redisUtil.getStringRedisTemplate();
      BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUS_CACHE_PREFIX);
      item.getList().stream().forEach(k -> {
        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();

        String key = item.getId().toString() + "_" + k.getSkuId().toString();

        Boolean hasKey = ops.hasKey(key);
        // 如果已经有key，直接返回
        if (hasKey) {
          return;
        }
        // 1.秒杀数据
        BeanUtils.copyProperties(k, redisTo);

        // 2.注入sku信息
        if (skuInfoVoMap.containsKey(k.getSkuId())) {
          redisTo.setSkuInfoVo(skuInfoVoMap.get(k.getSkuId()));
        }
        // 3.开始结束时间
        redisTo.setStartTime(item.getStartTime().getTime());
        redisTo.setEndTime(item.getEndTime().getTime());

        // 4.随机码
        String token = IdUtil.fastSimpleUUID();
        redisTo.setRandomCode(token);
        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
        // 5.sku信号量，保存库存
        semaphore.trySetPermits(redisTo.getSeckillCount().intValue());

        // 转为json存储到redis sku的key为sessionid_skuid(场次id+skuid)防止一天两次秒杀而相同的sku被覆盖
        ops.put(key, JSON.toJSONString(redisTo));

      });

    });


  }


}
