package com.yeye.mall.cart.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("mall-product")
public interface ProductFeignService {

  @GetMapping("/product/attr/info/{attrId}")
  R attrInfo(@PathVariable("attrId") Long attrId);

  @RequestMapping("/product/skuinfo/info/{skuId}")
  R info(@PathVariable("skuId") Long skuId);

  @RequestMapping("/product/skusaleattrvalue/saleattrlist/{skuId}")
  R querySaleAttrListById(@PathVariable("skuId") Long skuId);

  @PostMapping("/product/skuinfo/getSkuInfoByIds")
  R getSkuInfoByIds(@RequestBody List<Long> skuIds);
}
