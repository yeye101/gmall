package com.yeye.mall.order.feign;

import com.yeye.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient("mall-cart")
public interface CartFeignService {

  @GetMapping("/userCartItems/{memberId}")
  @ResponseBody
  public List<OrderItemVo> getUserCartItems(@PathVariable("memberId") Long memberId) ;

}
