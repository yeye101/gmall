package com.yeye.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.coupon.dao.SpuBoundsDao;
import com.yeye.mall.coupon.entity.SpuBoundsEntity;
import com.yeye.mall.coupon.service.SpuBoundsService;


@Service("spuBoundsService")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SpuBoundsEntity> page = this.page(
      new Query<SpuBoundsEntity>().getPage(params),
      new QueryWrapper<SpuBoundsEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public SpuBoundsEntity getBoundsBySpuId(Long spuId) {
    SpuBoundsEntity spuBoundsEntity = this.getOne(new LambdaQueryWrapper<SpuBoundsEntity>().eq(SpuBoundsEntity::getSpuId, spuId));
    return spuBoundsEntity;
  }

}