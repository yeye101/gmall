package com.yeye.mall.order.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.order.dao.OrderOperateHistoryDao;
import com.yeye.mall.order.entity.OrderOperateHistoryEntity;
import com.yeye.mall.order.service.OrderOperateHistoryService;


@Service("orderOperateHistoryService")
public class OrderOperateHistoryServiceImpl extends ServiceImpl<OrderOperateHistoryDao, OrderOperateHistoryEntity> implements OrderOperateHistoryService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<OrderOperateHistoryEntity> page = this.page(
      new Query<OrderOperateHistoryEntity>().getPage(params),
      new QueryWrapper<OrderOperateHistoryEntity>()
    );

    return new PageUtils(page);
  }

}