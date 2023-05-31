package com.yeye.mall.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.coupon.dao.SeckillSkuNoticeDao;
import com.yeye.mall.coupon.entity.SeckillSkuNoticeEntity;
import com.yeye.mall.coupon.service.SeckillSkuNoticeService;


@Service("seckillSkuNoticeService")
public class SeckillSkuNoticeServiceImpl extends ServiceImpl<SeckillSkuNoticeDao, SeckillSkuNoticeEntity> implements SeckillSkuNoticeService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SeckillSkuNoticeEntity> page = this.page(
      new Query<SeckillSkuNoticeEntity>().getPage(params),
      new QueryWrapper<SeckillSkuNoticeEntity>()
    );

    return new PageUtils(page);
  }

}