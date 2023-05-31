package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.CategoryEntity;
import com.yeye.mall.product.vo.web.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
public interface CategoryService extends IService<CategoryEntity> {

  PageUtils queryPage(Map<String, Object> params);

  List<CategoryEntity> listWithTree();

  void removeMenuByIds(List<Long> ids);

  /**
   * 查出三级分类完整路径
   */
  Long[] findCatelogPath(Long catelogId);

  void updateCascade(CategoryEntity category);

  List<CategoryEntity> getCategoryLeval1();

  Map<String, List<Catalog2Vo>> getCatalogJson();

}

