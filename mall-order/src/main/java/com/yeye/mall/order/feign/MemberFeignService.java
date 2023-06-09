package com.yeye.mall.order.feign;

import com.yeye.common.utils.R;
import com.yeye.mall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("mall-member")
public interface MemberFeignService {


  @GetMapping("/member/memberreceiveaddress/address/{memberId}")
  List<MemberAddressVo> getAddressById(@PathVariable("memberId") Long memberId);


  @RequestMapping("/member/member/info/{id}")
  R getMemberInfo(@PathVariable("id") Long id);
}
