package com.yeye.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.yeye.mall.product.service.SpuInfoService;
import com.yeye.mall.product.vo.SpuInfoVO;
import com.yeye.mall.product.vo.SpuSaveVo;
import com.yeye.mall.product.entity.SpuInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * spu信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
  @Autowired
  private SpuInfoService spuInfoService;

  /**
   * 商品spu上架/product/spuinfo/{spuId}/up
   */
  @PostMapping("/{spuId}/up")
  public R spuUp(@PathVariable("spuId") Long spuId) {
    spuInfoService.up(spuId);

    return R.ok();
  }

  /**
   * 商品spu下架/product/spuinfo/down/{spuId}
   */
  @PostMapping("/down/{spuId}")
  public R spuDown(@PathVariable("spuId") Long spuId) {
    spuInfoService.down(spuId);

    return R.ok();
  }


  @GetMapping("/spuInfo/{skuId}")
  public R getSpuInfo(@PathVariable("skuId") Long skuId) {
    SpuInfoVO vo = spuInfoService.getSpuInfo(skuId);

    return R.ok().put("data",vo);
  }


  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = spuInfoService.queryPageByCondition(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  public R info(@PathVariable("id") Long id) {
    SpuInfoEntity spuInfo = spuInfoService.getById(id);

    return R.ok().put("spuInfo", spuInfo);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  public R save(@RequestBody SpuSaveVo spuSaveVo) {
    spuInfoService.saveSpuInfo(spuSaveVo);
    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody SpuInfoEntity spuInfo) {
    spuInfoService.updateById(spuInfo);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] ids) {
    spuInfoService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
