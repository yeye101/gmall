package com.yeye.mall.product.service.impl;

import com.yeye.mall.product.dao.CommentReplayDao;
import com.yeye.mall.product.entity.CommentReplayEntity;
import com.yeye.mall.product.service.CommentReplayService;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;


@Service("commentReplayService")
public class CommentReplayServiceImpl extends ServiceImpl<CommentReplayDao, CommentReplayEntity> implements CommentReplayService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<CommentReplayEntity> page = this.page(
      new Query<CommentReplayEntity>().getPage(params),
      new QueryWrapper<CommentReplayEntity>()
    );

    return new PageUtils(page);
  }

}