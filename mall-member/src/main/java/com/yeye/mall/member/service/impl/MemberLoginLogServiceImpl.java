package com.yeye.mall.member.service.impl;

import com.yeye.mall.member.dao.MemberLoginLogDao;
import com.yeye.mall.member.entity.MemberLoginLogEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.member.service.MemberLoginLogService;


@Service("memberLoginLogService")
public class MemberLoginLogServiceImpl extends ServiceImpl<MemberLoginLogDao, MemberLoginLogEntity> implements MemberLoginLogService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<MemberLoginLogEntity> page = this.page(
      new Query<MemberLoginLogEntity>().getPage(params),
      new QueryWrapper<MemberLoginLogEntity>()
    );

    return new PageUtils(page);
  }

}