package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.CategoryBrandRelationEntity;
import com.yeye.mall.product.vo.BrandRespVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

  void updateBrand(Long brandId, String name);

  void updateCategory(Long catId, String name);

  List<BrandRespVo> getRelationBrandsList(Long catId);
}

