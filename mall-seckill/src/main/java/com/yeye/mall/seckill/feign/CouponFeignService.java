package com.yeye.mall.seckill.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("mall-coupon")
public interface CouponFeignService {

  @GetMapping("/coupon/spubounds/info/{spuId}")
  R getBoundsBySpuId(@PathVariable("spuId") Long spuId);


  @GetMapping("/coupon/seckillsession/lates3DaysSession")
  R getLates3DaysSession();
}


