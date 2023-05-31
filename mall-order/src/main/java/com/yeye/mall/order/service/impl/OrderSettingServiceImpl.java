package com.yeye.mall.order.service.impl;

import com.yeye.mall.order.dao.OrderSettingDao;
import com.yeye.mall.order.entity.OrderSettingEntity;
import com.yeye.mall.order.service.OrderSettingService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;


@Service("orderSettingService")
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingDao, OrderSettingEntity> implements OrderSettingService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<OrderSettingEntity> page = this.page(
      new Query<OrderSettingEntity>().getPage(params),
      new QueryWrapper<OrderSettingEntity>()
    );

    return new PageUtils(page);
  }

}