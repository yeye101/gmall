package com.yeye.mall.product.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("mall-seckill")
public interface SeckillFeignService {

  @GetMapping("/seckill/skuSeckillInfo/{skuId}")
  public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
