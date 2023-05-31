package com.yeye.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:03
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

  PageUtils queryPage(Map<String, Object> params);

  List<SeckillSessionEntity> getLates3DaysSession();

}

