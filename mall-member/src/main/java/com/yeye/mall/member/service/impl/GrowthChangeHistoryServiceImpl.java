package com.yeye.mall.member.service.impl;

import com.yeye.mall.member.dao.GrowthChangeHistoryDao;
import com.yeye.mall.member.entity.GrowthChangeHistoryEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.member.service.GrowthChangeHistoryService;


@Service("growthChangeHistoryService")
public class GrowthChangeHistoryServiceImpl extends ServiceImpl<GrowthChangeHistoryDao, GrowthChangeHistoryEntity> implements GrowthChangeHistoryService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<GrowthChangeHistoryEntity> page = this.page(
      new Query<GrowthChangeHistoryEntity>().getPage(params),
      new QueryWrapper<GrowthChangeHistoryEntity>()
    );

    return new PageUtils(page);
  }

}