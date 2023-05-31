package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface BrandService extends IService<BrandEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void updateDetail(BrandEntity brand);
}

