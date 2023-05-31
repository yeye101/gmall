package com.yeye.mall.order.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {

  @GetMapping("/product/attr/info/{attrId}")
  R attrInfo(@PathVariable("attrId") Long attrId);

  @RequestMapping("/product/skuinfo/info/{skuId}")
  R getSkuInfo(@PathVariable("skuId") Long skuId);

  @RequestMapping("/product/skusaleattrvalue/saleattrlist/{skuId}")
  R querySaleAttrListById(@PathVariable("skuId") Long skuId);

  @GetMapping("/product/spuinfo/spuInfo/{skuId}")
  R getSpuInfo(@PathVariable("skuId") Long skuId);

}
