package com.yeye.mall.product.vo.web;

import com.yeye.mall.product.entity.SkuImagesEntity;
import com.yeye.mall.product.entity.SkuInfoEntity;
import com.yeye.mall.product.entity.SpuInfoDescEntity;
import com.yeye.mall.product.vo.SeckillInfoVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SkuItemVo {

  // sku的信息 pms_sku_info
  private SkuInfoEntity info;

  // sku图片 pms_sku_images
  private List<SkuImagesEntity> images;

  // spu销售组合
  private List<SkuItemSaleAttrsVo> saleAttrsVo;

  // spu信息
  private SpuInfoDescEntity spuInfoDesc;
  // spu规格
  private List<SpuItemGroupVo>  groupVos;

  Boolean hasStock =Boolean.TRUE;

  private SeckillInfoVo seckillInfoVo;
}
