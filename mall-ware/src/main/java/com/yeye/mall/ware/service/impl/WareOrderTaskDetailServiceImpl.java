package com.yeye.mall.ware.service.impl;

import com.yeye.mall.ware.dao.WareOrderTaskDetailDao;
import com.yeye.mall.ware.entity.WareOrderTaskDetailEntity;
import com.yeye.mall.ware.service.WareOrderTaskDetailService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<WareOrderTaskDetailEntity> page = this.page(
      new Query<WareOrderTaskDetailEntity>().getPage(params),
      new QueryWrapper<WareOrderTaskDetailEntity>()
    );

    return new PageUtils(page);
  }

}