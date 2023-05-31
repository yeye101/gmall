package com.yeye.mall.authserver.feign;

import com.yeye.common.utils.R;
import com.yeye.mall.authserver.vo.UserLoginVo;
import com.yeye.mall.authserver.vo.UserRegisterVo;
import com.yeye.mall.authserver.vo.UserSocialLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-member")
public interface MemberFeignService {

  @PostMapping("/member/member/register")
  R memberRegister(@RequestBody UserRegisterVo vo);

  @PostMapping("/member/member/login")
  R memberLogin(@RequestBody UserLoginVo vo);

  @PostMapping("/member/member/socialLogin")
  R memberSocialLogin(@RequestBody UserSocialLoginVo vo);
}
