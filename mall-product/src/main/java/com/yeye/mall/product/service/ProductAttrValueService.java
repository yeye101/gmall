package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveProductAttr(List<ProductAttrValueEntity> productAttrValueEntities);

  List<ProductAttrValueEntity> baseSpuAttrList(Long spuId);

  void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entityList);

}

