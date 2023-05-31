package com.yeye.mall.order.service.impl;

import com.yeye.mall.order.dao.RefundInfoDao;
import com.yeye.mall.order.entity.RefundInfoEntity;
import com.yeye.mall.order.service.RefundInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;


@Service("refundInfoService")
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoDao, RefundInfoEntity> implements RefundInfoService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<RefundInfoEntity> page = this.page(
      new Query<RefundInfoEntity>().getPage(params),
      new QueryWrapper<RefundInfoEntity>()
    );

    return new PageUtils(page);
  }

}