package com.yeye.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yeye.mall.product.entity.CategoryEntity;
import com.yeye.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yeye.common.utils.R;


/**
 * 商品三级分类
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;

  /**
   * 列表
   */
  @RequestMapping("/list/tree")
  public R list(@RequestParam Map<String, Object> params) {

    List<CategoryEntity> entityList = categoryService.listWithTree();

    return R.ok().put("data", entityList);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{catId}")
  public R info(@PathVariable("catId") Long catId) {
    CategoryEntity category = categoryService.getById(catId);

    return R.ok().put("data", category);
  }

  /**
   * 保存
   */
  @CacheEvict(value = "category", allEntries = true)
  @RequestMapping("/save")
  public R save(@RequestBody CategoryEntity category) {
    categoryService.save(category);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody CategoryEntity category) {
    categoryService.updateCascade(category);

    return R.ok();
  }

  //批量修改，参数要传数组，不能传list
  @CacheEvict(value = "category", allEntries = true)
  @RequestMapping("/update/sort")
  public R updateSort(@RequestBody CategoryEntity[] category) {
    categoryService.updateBatchById(Arrays.asList(category));
    return R.ok();
  }

  /**
   * 删除
   */
  @CacheEvict(value = "category", allEntries = true)
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] catIds) {
    categoryService.removeMenuByIds(Arrays.asList(catIds));

    return R.ok();
  }

}
