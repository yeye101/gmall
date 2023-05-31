package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.product.dao.SkuSaleAttrValueDao;
import com.yeye.mall.product.entity.SkuSaleAttrValueEntity;
import com.yeye.mall.product.service.SkuSaleAttrValueService;
import com.yeye.mall.product.vo.web.SkuItemSaleAttrsVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SkuSaleAttrValueEntity> page = this.page(
      new Query<SkuSaleAttrValueEntity>().getPage(params),
      new QueryWrapper<SkuSaleAttrValueEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public List<SkuItemSaleAttrsVo> getSaleAttrsVoBySId(Long spuId) {
    List<SkuItemSaleAttrsVo> result = new ArrayList<>();

    result = this.baseMapper.getSaleAttrsVoBySId(spuId);

    return result;
  }

  @Override
  public List<String> querySaleAttrListById(Long skuId) {
    List<String> saleAttrList = new ArrayList<>();


    List<SkuSaleAttrValueEntity> saleAttrValueList = this.list(new LambdaQueryWrapper<SkuSaleAttrValueEntity>()
      .eq(SkuSaleAttrValueEntity::getSkuId, skuId));

    if (CollectionUtils.isEmpty(saleAttrValueList)) {
      return saleAttrList;
    }

    saleAttrList = saleAttrValueList.stream()
      .map(item -> item.getAttrName() + ":" + item.getAttrValue())
      .collect(Collectors.toList());


    return saleAttrList;
  }

}