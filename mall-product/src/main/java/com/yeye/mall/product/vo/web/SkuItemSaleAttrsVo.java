package com.yeye.mall.product.vo.web;

import lombok.Data;

import java.util.List;


@Data
public class SkuItemSaleAttrsVo {

  private Long attrId;

  private String attrName;

  private List<SkuSaleAttrWithIdVo> attrValues;
}
