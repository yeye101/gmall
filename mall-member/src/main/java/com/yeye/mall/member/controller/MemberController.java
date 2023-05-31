package com.yeye.mall.member.controller;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.member.entity.MemberEntity;
import com.yeye.mall.member.feign.CouponFeignService;
import com.yeye.mall.member.service.MemberService;
import com.yeye.mall.member.vo.MemberLoginVo;
import com.yeye.mall.member.vo.MemberRegisterVo;
import com.yeye.mall.member.vo.MemberSocialLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:36:54
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
  @Autowired
  private MemberService memberService;

  @PostMapping("/register")
  public R memberRegister(@RequestBody MemberRegisterVo vo) {

    return memberService.register(vo);

  }

  @PostMapping("/login")
  public R memberLogin(@RequestBody MemberLoginVo vo) {

    return memberService.login(vo);
  }

  @PostMapping("/socialLogin")
  public R memberSocialLogin(@RequestBody MemberSocialLoginVo vo) {

    return memberService.socialLogin(vo);
  }


  /**
   * 列表
   */
  @RequestMapping("/list")

  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = memberService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    MemberEntity member = memberService.getById(id);

    return R.ok().put("member", member);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody MemberEntity member) {
    memberService.save(member);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody MemberEntity member) {
    memberService.updateById(member);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    memberService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
