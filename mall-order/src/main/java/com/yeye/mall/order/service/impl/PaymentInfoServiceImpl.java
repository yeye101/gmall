package com.yeye.mall.order.service.impl;

import com.yeye.mall.order.service.PaymentInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.order.dao.PaymentInfoDao;
import com.yeye.mall.order.entity.PaymentInfoEntity;


@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoDao, PaymentInfoEntity> implements PaymentInfoService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<PaymentInfoEntity> page = this.page(
      new Query<PaymentInfoEntity>().getPage(params),
      new QueryWrapper<PaymentInfoEntity>()
    );

    return new PageUtils(page);
  }

}