package com.yeye.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeye.mall.product.vo.BrandRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yeye.mall.product.entity.CategoryBrandRelationEntity;
import com.yeye.mall.product.service.CategoryBrandRelationService;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
  @Autowired
  private CategoryBrandRelationService categoryBrandRelationService;

  /**
   * 获取当前f分类关联的brand列表/product/categorybrandrelation/brands/list
   */
  @GetMapping("/brands/list")
  public R relationBrandsList(@RequestParam("catId") Long catId) {
    List<BrandRespVo> data = categoryBrandRelationService.getRelationBrandsList(catId);

    return R.ok().put("data", data);
  }


  /**
   * 获取当前brand关联的列表
   */
  @GetMapping("/catelog/list")
  public R catelogList(@RequestParam("brandId") Long brandId) {
    List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
      new LambdaQueryWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getBrandId, brandId));

    return R.ok().put("data", data);
  }

  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = categoryBrandRelationService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  public R info(@PathVariable("id") Long id) {
    CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

    return R.ok().put("categoryBrandRelation", categoryBrandRelation);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {

    categoryBrandRelationService.saveDetail(categoryBrandRelation);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
    categoryBrandRelationService.updateById(categoryBrandRelation);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] ids) {
    categoryBrandRelationService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
