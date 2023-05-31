package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.SkuSaleAttrValueEntity;
import com.yeye.mall.product.vo.Attr;
import com.yeye.mall.product.vo.web.SkuItemSaleAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

  PageUtils queryPage(Map<String, Object> params);

  List<SkuItemSaleAttrsVo> getSaleAttrsVoBySId(Long spuId);

  List<String> querySaleAttrListById(Long skuId);
}

