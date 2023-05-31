package com.yeye.mall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yeye.mall.member.entity.MemberReceiveAddressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yeye.mall.member.service.MemberReceiveAddressService;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 会员收货地址
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:36:54
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
  @Autowired
  private MemberReceiveAddressService memberReceiveAddressService;


  @GetMapping("address/{memberId}")
  public List<MemberReceiveAddressEntity> getAddressById(@PathVariable("memberId") Long memberId) {
    List<MemberReceiveAddressEntity>  data = memberReceiveAddressService.getAddressById(memberId);
    return data;
  }


  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = memberReceiveAddressService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);

    return R.ok().put("memberReceiveAddress", memberReceiveAddress);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
    memberReceiveAddressService.save(memberReceiveAddress);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
    memberReceiveAddressService.updateById(memberReceiveAddress);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    memberReceiveAddressService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
