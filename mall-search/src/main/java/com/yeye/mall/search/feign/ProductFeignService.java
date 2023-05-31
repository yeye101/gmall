package com.yeye.mall.search.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {

  @GetMapping("/product/attr/info/{attrId}")
  public R attrInfo(@PathVariable("attrId") Long attrId) ;
}
