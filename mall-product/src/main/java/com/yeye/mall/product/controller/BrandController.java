package com.yeye.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.yeye.common.valid.AddGroup;
import com.yeye.common.valid.UpdateGroup;
import com.yeye.common.valid.UpdateStatusGroup;
import com.yeye.mall.product.entity.BrandEntity;
import com.yeye.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 品牌
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
  @Autowired
  private BrandService brandService;

  /**
   * 列表
   */
  @RequestMapping("/list")

  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = brandService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{brandId}")

  public R info(@PathVariable("brandId") Long brandId) {
    BrandEntity brand = brandService.getById(brandId);

    return R.ok().put("brand", brand);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand) {
    brandService.save(brand);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand) {
    brandService.updateDetail(brand);

    return R.ok();
  }

  /**
   * 修改状态信息showStatus
   */
  @RequestMapping("/update/status")

  public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand) {
    brandService.updateById(brand);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] brandIds) {
    brandService.removeByIds(Arrays.asList(brandIds));

    return R.ok();
  }

}
