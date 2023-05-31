package com.yeye.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.ware.dao.WareOrderTaskDao;
import com.yeye.mall.ware.entity.WareOrderTaskEntity;
import com.yeye.mall.ware.service.WareOrderTaskService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<WareOrderTaskEntity> page = this.page(
      new Query<WareOrderTaskEntity>().getPage(params),
      new QueryWrapper<WareOrderTaskEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public WareOrderTaskEntity getOrderTaskEntityBySn(String orderSn) {

    WareOrderTaskEntity entity = this.getOne(
      new LambdaQueryWrapper<WareOrderTaskEntity>()
        .eq(WareOrderTaskEntity::getOrderSn, orderSn));
    return entity;

  }

}