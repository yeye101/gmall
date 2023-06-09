package com.yeye.mall.coupon.service.impl;

import com.yeye.mall.coupon.entity.HomeSubjectEntity;
import com.yeye.mall.coupon.service.HomeSubjectService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.coupon.dao.HomeSubjectDao;


@Service("homeSubjectService")
public class HomeSubjectServiceImpl extends ServiceImpl<HomeSubjectDao, HomeSubjectEntity> implements HomeSubjectService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<HomeSubjectEntity> page = this.page(
      new Query<HomeSubjectEntity>().getPage(params),
      new QueryWrapper<HomeSubjectEntity>()
    );

    return new PageUtils(page);
  }

}