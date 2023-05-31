package com.yeye.mall.member.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("mall-order")
public interface OrderFeignService {

  @PostMapping("/order/order/listWithItems")
  R listWithItems(@RequestBody Map<String, Object> params);
}
