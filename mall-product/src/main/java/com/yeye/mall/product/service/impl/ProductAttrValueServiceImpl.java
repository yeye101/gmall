package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeye.mall.product.entity.ProductAttrValueEntity;
import com.yeye.mall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.product.dao.ProductAttrValueDao;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<ProductAttrValueEntity> page = this.page(
      new Query<ProductAttrValueEntity>().getPage(params),
      new QueryWrapper<ProductAttrValueEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public void saveProductAttr(List<ProductAttrValueEntity> productAttrValueEntities) {
    this.saveBatch(productAttrValueEntities);
  }

  @Override
  public List<ProductAttrValueEntity> baseSpuAttrList(Long spuId) {
    List<ProductAttrValueEntity> list = this.list(new LambdaQueryWrapper<ProductAttrValueEntity>()
      .eq(ProductAttrValueEntity::getSpuId, spuId));
    return list;
  }

  @Transactional
  @Override
  public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entityList) {
    // 因为可能会有没有添加过的属性，所以先删除，再添加
    this.baseMapper.delete(new LambdaQueryWrapper<ProductAttrValueEntity>()
      .eq(ProductAttrValueEntity::getSpuId, spuId));

    List<ProductAttrValueEntity> collect = entityList.stream().peek(item -> {
      item.setSpuId(spuId);
    }).collect(Collectors.toList());

    this.saveBatch(collect);

  }

}