package com.yeye.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:04
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

