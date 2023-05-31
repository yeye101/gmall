package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeye.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.product.dao.BrandDao;
import com.yeye.mall.product.entity.BrandEntity;
import com.yeye.mall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


  @Autowired
  CategoryBrandRelationService categoryBrandRelationService;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {

    String key = (String) params.get("key");
    LambdaQueryWrapper<BrandEntity> wrapper = new LambdaQueryWrapper<BrandEntity>();
    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(BrandEntity::getBrandId, key).or().like(BrandEntity::getName, key);
    }
    IPage<BrandEntity> page = this.page(
      new Query<BrandEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);
  }

  /**
   * 开启事务统一管理
   *
   * @param brand
   */
  @Transactional
  @Override
  public void updateDetail(BrandEntity brand) {

    this.updateById(brand);

    if (!StringUtils.isEmpty(brand.getName())) {
      categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
    }

    //todo 冗余其他字段的操作
  }

}