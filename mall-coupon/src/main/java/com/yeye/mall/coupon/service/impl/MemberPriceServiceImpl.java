package com.yeye.mall.coupon.service.impl;

import com.yeye.mall.coupon.service.MemberPriceService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.coupon.dao.MemberPriceDao;
import com.yeye.mall.coupon.entity.MemberPriceEntity;


@Service("memberPriceService")
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceDao, MemberPriceEntity> implements MemberPriceService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<MemberPriceEntity> page = this.page(
      new Query<MemberPriceEntity>().getPage(params),
      new QueryWrapper<MemberPriceEntity>()
    );

    return new PageUtils(page);
  }

}