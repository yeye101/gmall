package com.yeye.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.to.SkuReductionTo;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:04
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveSkuReduction(SkuReductionTo skuReductionTo);
}

