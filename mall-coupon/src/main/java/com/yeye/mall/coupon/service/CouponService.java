package com.yeye.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:03
 */
public interface CouponService extends IService<CouponEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

