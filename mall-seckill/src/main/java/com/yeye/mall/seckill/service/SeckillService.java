package com.yeye.mall.seckill.service;

import com.yeye.mall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

public interface SeckillService {


  void uploadSeckillSku3Days();

  List<SeckillSkuRedisTo> getCurrentSeckillSkus();

  SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

  String seckill(String id, String randomCode, Integer num);
}
