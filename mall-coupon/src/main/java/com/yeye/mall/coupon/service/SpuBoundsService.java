package com.yeye.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:04
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

  PageUtils queryPage(Map<String, Object> params);

  SpuBoundsEntity getBoundsBySpuId(Long spuId);
}

