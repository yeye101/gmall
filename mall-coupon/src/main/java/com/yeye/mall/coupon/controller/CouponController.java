package com.yeye.mall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yeye.mall.coupon.entity.CouponEntity;
import com.yeye.mall.coupon.service.CouponService;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 优惠券信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:03
 */
@RestController
@RequestMapping("coupon/coupon")
@RefreshScope
public class CouponController {
  @Autowired
  private CouponService couponService;

  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = couponService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    CouponEntity coupon = couponService.getById(id);

    return R.ok().put("coupon", coupon);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody CouponEntity coupon) {
    couponService.save(coupon);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody CouponEntity coupon) {
    couponService.updateById(coupon);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    couponService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
