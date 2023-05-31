package com.yeye.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:04
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

