package com.yeye.mall.member.service.impl;

import com.yeye.mall.member.dao.MemberLevelDao;
import com.yeye.mall.member.entity.MemberLevelEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.member.service.MemberLevelService;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<MemberLevelEntity> page = this.page(
      new Query<MemberLevelEntity>().getPage(params),
      new QueryWrapper<MemberLevelEntity>()
    );

    return new PageUtils(page);
  }

}