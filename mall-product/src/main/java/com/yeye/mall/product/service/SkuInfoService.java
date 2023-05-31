package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.SkuInfoEntity;
import com.yeye.mall.product.vo.web.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveSkuInfo(SkuInfoEntity skuInfoEntity);

  PageUtils queryPageByCondition(Map<String, Object> params);

  List<SkuInfoEntity> getSkusBySpuId(Long spuId);

  SkuItemVo queryItem(Long skuId);
}

