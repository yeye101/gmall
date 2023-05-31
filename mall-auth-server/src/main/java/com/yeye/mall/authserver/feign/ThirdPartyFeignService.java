package com.yeye.mall.authserver.feign;

import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("mall-thirdparty")
public interface ThirdPartyFeignService {

  @GetMapping("/sms/sendCode")
  R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
