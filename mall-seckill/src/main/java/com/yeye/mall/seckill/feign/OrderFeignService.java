package com.yeye.mall.seckill.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("mall-order")
public interface OrderFeignService {


  @GetMapping("/order/order/getOrderInfoByOrderSn/{orderSn}")
  R getOrderInfoByOrderSn(@PathVariable("orderSn") String orderSn);


}
