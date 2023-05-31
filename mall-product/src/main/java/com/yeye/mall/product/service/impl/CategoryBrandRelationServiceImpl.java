package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yeye.mall.product.dao.BrandDao;
import com.yeye.mall.product.dao.CategoryDao;
import com.yeye.mall.product.entity.BrandEntity;
import com.yeye.mall.product.entity.CategoryEntity;
import com.yeye.mall.product.vo.BrandRespVo;
import com.yeye.mall.product.entity.CategoryBrandRelationEntity;
import com.yeye.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.product.dao.CategoryBrandRelationDao;
import org.springframework.util.CollectionUtils;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

  @Autowired
  BrandDao brandDao;

  @Autowired
  CategoryDao categoryDao;


  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<CategoryBrandRelationEntity> page = this.page(
      new Query<CategoryBrandRelationEntity>().getPage(params),
      new QueryWrapper<CategoryBrandRelationEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
    Long brandId = categoryBrandRelation.getBrandId();
    Long catelogId = categoryBrandRelation.getCatelogId();
    // 查询详细信息
    BrandEntity brandEntity = brandDao.selectById(brandId);
    CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

    if (null == brandEntity) {
      return;
    }
    if (null == categoryEntity) {
      return;
    }
    categoryBrandRelation.setBrandName(brandEntity.getName());
    categoryBrandRelation.setCatelogName(categoryEntity.getName());
    this.save(categoryBrandRelation);

  }

  @Override
  public void updateBrand(Long brandId, String name) {
    CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();

    entity.setBrandId(brandId);
    entity.setBrandName(name);

    this.update(entity, new LambdaUpdateWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getBrandId, brandId));
  }

  @Override
  public void updateCategory(Long catId, String name) {

    this.baseMapper.updateCategory(catId, name);

  }

  @Override
  public List<BrandRespVo> getRelationBrandsList(Long catId) {


    List<CategoryBrandRelationEntity> data = this.list(
      new LambdaQueryWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getCatelogId, catId));

    if (CollectionUtils.isEmpty(data)){
      return null;
    }

    List<BrandRespVo> voList = data.stream().map(item -> {
      BrandRespVo vo = new BrandRespVo();
      BeanUtils.copyProperties(item, vo);
      return vo;
    }).collect(Collectors.toList());

    return voList;
  }

}