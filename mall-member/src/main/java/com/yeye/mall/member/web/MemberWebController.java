package com.yeye.mall.member.web;

import com.yeye.common.to.MemberRes;
import com.yeye.common.utils.Constant;
import com.yeye.common.utils.R;
import com.yeye.mall.member.feign.OrderFeignService;
import com.yeye.mall.member.interceptor.LoginUserInterceptor;
import com.yeye.mall.member.service.MemberReceiveAddressService;
import com.yeye.mall.member.service.MemberService;
import com.yeye.mall.member.vo.MemberAddressSaveVo;
import com.yeye.mall.member.vo.MemberInfoVo;
import com.yeye.mall.member.vo.MemberModifyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@Controller
public class MemberWebController {

  private final static String MEMBER_INFO_PAGE = "redirect:http://member.mall-yeye.com/memberInfo.html";


  @Autowired
  private OrderFeignService orderFeignService;

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberReceiveAddressService memberReceiveAddressService;


  @GetMapping("/memberOrderList.html")
  public String memberOrderList(@RequestParam(value = "pageNum", defaultValue = "1") String pageNum,
                                Model model) {

    HashMap<String, Object> params = new HashMap<>();
    params.put(Constant.PAGE, pageNum);
    params.put(Constant.LIMIT, "10");
    R r = orderFeignService.listWithItems(params);
    model.addAttribute("orders", r);
    return "memberOrderList";
  }

  @GetMapping("/memberInfo.html")
  public String memberInfo(Model model) {
    MemberRes memberRes = LoginUserInterceptor.loginUser.get();
    MemberInfoVo memberEntity = memberService.getMemberInfo(memberRes.getId());

    model.addAttribute("member", memberEntity);
    return "memberInfo";
  }

  @PostMapping("/member/modify")
  public String memberModify(@Valid MemberModifyVo vo, Model model) {

    R r = memberService.memberModify(vo);
    return MEMBER_INFO_PAGE;
  }

  @PostMapping("/member/address/save")
  @ResponseBody
  public R memberAddressSave(@RequestBody @Valid MemberAddressSaveVo vo) {

    return memberReceiveAddressService.memberModify(vo);
  }


}
