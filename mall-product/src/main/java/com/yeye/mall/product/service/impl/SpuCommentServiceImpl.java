package com.yeye.mall.product.service.impl;

import com.yeye.mall.product.dao.SpuCommentDao;
import com.yeye.mall.product.service.SpuCommentService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.product.entity.SpuCommentEntity;


@Service("spuCommentService")
public class SpuCommentServiceImpl extends ServiceImpl<SpuCommentDao, SpuCommentEntity> implements SpuCommentService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SpuCommentEntity> page = this.page(
      new Query<SpuCommentEntity>().getPage(params),
      new QueryWrapper<SpuCommentEntity>()
    );

    return new PageUtils(page);
  }

}