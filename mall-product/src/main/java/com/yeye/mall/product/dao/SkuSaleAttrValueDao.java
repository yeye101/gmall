package com.yeye.mall.product.dao;

import com.yeye.mall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeye.mall.product.vo.Attr;
import com.yeye.mall.product.vo.web.SkuItemSaleAttrsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

  List<SkuItemSaleAttrsVo> getSaleAttrsVoBySId(@Param("spuId") Long spuId);
}
