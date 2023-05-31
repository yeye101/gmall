package com.yeye.mall.product.controller;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.product.entity.ProductAttrValueEntity;
import com.yeye.mall.product.service.AttrService;
import com.yeye.mall.product.service.ProductAttrValueService;
import com.yeye.mall.product.vo.AttrRespVo;
import com.yeye.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品属性
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
  @Autowired
  private AttrService attrService;

  @Autowired
  ProductAttrValueService productAttrValueService;

  /**
   * 获取spu规格属性 /product/attr/base/listforspu/{spuId}
   */
  @GetMapping("/base/listforspu/{spuId}")
  public R baseSpuAttrList(@PathVariable("spuId") Long spuId) {
    List<ProductAttrValueEntity> data = productAttrValueService.baseSpuAttrList(spuId);

    return R.ok().put("data", data);
  }


  /**
   * 销售属性列表  /product/attr/sale/list/{catelogId}
   * 规格属性列表  /product/attr/base/list/{catelogId}
   */
  @GetMapping("/{attrType}/list/{catelogId}")
  public R list(@RequestParam Map<String, Object> params,
                @PathVariable("catelogId") Long catelogId,
                @PathVariable("attrType") String attrType) {
    PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);

    return R.ok().put("page", page);
  }

  /**
   * 信息
   */
  @RequestMapping("/info/{attrId}")
  public R info(@PathVariable("attrId") Long attrId) {
    AttrRespVo respVo = attrService.getAttrInfo(attrId);

    return R.ok().put("attr", respVo);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  public R save(@RequestBody AttrVo attr) {

    attrService.saveAttr(attr);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody AttrVo attr) {
    attrService.updateAttr(attr);

    return R.ok();
  }

  /**
   * 修改商品规格 /product/attr/update/{spuId}
   */
  @PostMapping("/update/{spuId}")
  public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                         @RequestBody List<ProductAttrValueEntity> entityList) {
    productAttrValueService.updateSpuAttr(spuId, entityList);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] attrIds) {
    attrService.removeByIds(Arrays.asList(attrIds));

    return R.ok();
  }


}
