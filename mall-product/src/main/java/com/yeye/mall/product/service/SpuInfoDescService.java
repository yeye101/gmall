package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveSpuInfoDesc(SpuInfoDescEntity descEntity);
}

