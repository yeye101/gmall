package com.yeye.mall.order.feign;

import com.yeye.common.utils.R;
import com.yeye.mall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("mall-ware")
public interface WareFeignService {


  @GetMapping("/ware/waresku/hasstock")
  R getSkuHasStock(@RequestBody List<Long> skuIds);

  @GetMapping("/ware/wareinfo/getFare")
   R getFare(@RequestParam("addrId")  Long addrId);

  @PostMapping("/ware/waresku/lock/order")
  R lockOrder(@RequestBody WareSkuLockVo vo);
}
