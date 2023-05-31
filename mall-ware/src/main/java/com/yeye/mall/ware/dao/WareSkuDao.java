package com.yeye.mall.ware.dao;

import com.yeye.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeye.mall.ware.vo.SkuHasStockVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

  void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

  List<SkuHasStockVo> getSkuStock(@Param("skuIds") List<Long> skuIds);

  List<Long> selectWareIdsBySkuId(@Param("skuId") Long skuId);

  Integer lockSkuStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("num") Integer num);

  void stockLockedRelease(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}
