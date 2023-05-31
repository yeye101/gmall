package com.yeye.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeye.mall.coupon.entity.SeckillSkuRelationEntity;
import com.yeye.mall.coupon.service.SeckillSessionService;
import com.yeye.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;

import com.yeye.mall.coupon.dao.SeckillSessionDao;
import com.yeye.mall.coupon.entity.SeckillSessionEntity;
import org.springframework.util.CollectionUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

  @Autowired
  private SeckillSkuRelationService seckillSkuRelationService;


  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SeckillSessionEntity> page = this.page(
      new Query<SeckillSessionEntity>().getPage(params),
      new QueryWrapper<SeckillSessionEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public List<SeckillSessionEntity> getLates3DaysSession() {


    String startTime = getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String endTime = getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    List<SeckillSessionEntity> list = this.list(new LambdaQueryWrapper<SeckillSessionEntity>()
      .between(SeckillSessionEntity::getStartTime, startTime, endTime)
      .or()
      .between(SeckillSessionEntity::getEndTime, startTime, endTime)
    );

    if (CollectionUtils.isEmpty(list)) {
      return list;
    }

    list = list.stream().peek(item -> {
      List<SeckillSkuRelationEntity> relationEntityList = this.seckillSkuRelationService.list(
        new LambdaQueryWrapper<SeckillSkuRelationEntity>()
          .eq(SeckillSkuRelationEntity::getPromotionSessionId, item.getId()));
      item.setList(relationEntityList);
    }).collect(Collectors.toList());


    return list;
  }

  private LocalDateTime getEndTime() {
    LocalDate plus2 = LocalDate.now().plusDays(2);
    return LocalDateTime.of(plus2, LocalTime.MAX);
  }

  private LocalDateTime getStartTime() {
    LocalDate now = LocalDate.now();
    return LocalDateTime.of(now, LocalTime.MIN);
  }

}