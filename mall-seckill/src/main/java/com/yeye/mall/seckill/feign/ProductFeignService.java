package com.yeye.mall.seckill.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("mall-product")
public interface ProductFeignService {

  @RequestMapping("/product/skuinfo/info/{skuId}")
  public R getSkuInfo(@PathVariable("skuId") Long skuId);

  @PostMapping("/product/skuinfo/getSkuInfoByIds")
  public R getSkuInfoByIds(@RequestBody List<Long> skuIds);
}
