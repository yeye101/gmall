package com.yeye.mall.product.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-ware")
public interface WareFeignService {

  //查询sku是否有库存
  @PostMapping("/ware/waresku/hasstock")
  public R getSkuHasStock(@RequestBody List<Long> skuIds);
  
}
