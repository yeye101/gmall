package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.SpuInfoEntity;
import com.yeye.mall.product.vo.SpuInfoVO;
import com.yeye.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveSpuInfo(SpuSaveVo spuSaveVo);

  void saveBaseSpuInfo(SpuInfoEntity infoEntity);

  PageUtils queryPageByCondition(Map<String, Object> params);

  void up(Long spuId);

  SpuInfoVO getSpuInfo(Long skuId);

  void down(Long spuId);
}

