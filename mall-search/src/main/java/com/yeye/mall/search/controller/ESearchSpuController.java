package com.yeye.mall.search.controller;

import com.yeye.common.exception.BizCodeEnum;
import com.yeye.common.to.es.SkuEsModel;
import com.yeye.common.utils.R;
import com.yeye.mall.search.service.ESearchSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("search")
public class ESearchSpuController {

  @Autowired
  ESearchSpuService eSearchSpuService;

  // 上架商品
  @PostMapping("/upProduct")
  public R upProduct(@RequestBody List<SkuEsModel> skuEsModels) {
    Boolean aBoolean = eSearchSpuService.upProduct(skuEsModels);
    if (aBoolean) {
      return R.ok();
    } else {
      return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }
  }

  // 下架商品
  @PostMapping("/downProduct/{spuId}")
  public R downProduct(@PathVariable("spuId") Long spuId) {
    Boolean aBoolean = eSearchSpuService.downProduct(spuId);
    if (aBoolean) {
      return R.ok();
    } else {
      return R.error(BizCodeEnum.PRODUCT_DOWN_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_DOWN_EXCEPTION.getMsg());
    }
  }

  // 下架商品
  @PostMapping("/product/{catelogId}")
  public R getSpuProduct(@PathVariable("catelogId") Long catelogId) {
    return eSearchSpuService.getSpuProduct(catelogId);
  }

}
