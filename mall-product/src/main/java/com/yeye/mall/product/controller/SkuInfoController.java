package com.yeye.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yeye.mall.product.entity.SkuInfoEntity;
import com.yeye.mall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * sku信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
  @Autowired
  private SkuInfoService skuInfoService;

  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = skuInfoService.queryPageByCondition(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{skuId}")
  public R info(@PathVariable("skuId") Long skuId) {
    SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

    return R.ok().put("skuInfo", skuInfo);
  }

  @PostMapping("/getSkuInfoByIds")
  public R getSkuInfoByIds(@RequestBody List<Long> skuIds) {
    List<SkuInfoEntity> data = skuInfoService.listByIds(skuIds);

    return R.ok().put("data", data);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  public R save(@RequestBody SkuInfoEntity skuInfo) {
    skuInfoService.save(skuInfo);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody SkuInfoEntity skuInfo) {
    skuInfoService.updateById(skuInfo);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] skuIds) {
    skuInfoService.removeByIds(Arrays.asList(skuIds));

    return R.ok();
  }

}
