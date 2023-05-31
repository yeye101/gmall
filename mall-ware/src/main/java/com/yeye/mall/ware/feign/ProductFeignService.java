package com.yeye.mall.ware.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {

  @RequestMapping("/product/skuinfo/info/{skuId}")
  public R info(@PathVariable("skuId") Long skuId);


}
