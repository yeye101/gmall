package com.yeye.mall.order.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.order.dao.OrderReturnApplyDao;
import com.yeye.mall.order.entity.OrderReturnApplyEntity;
import com.yeye.mall.order.service.OrderReturnApplyService;


@Service("orderReturnApplyService")
public class OrderReturnApplyServiceImpl extends ServiceImpl<OrderReturnApplyDao, OrderReturnApplyEntity> implements OrderReturnApplyService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<OrderReturnApplyEntity> page = this.page(
      new Query<OrderReturnApplyEntity>().getPage(params),
      new QueryWrapper<OrderReturnApplyEntity>()
    );

    return new PageUtils(page);
  }

}