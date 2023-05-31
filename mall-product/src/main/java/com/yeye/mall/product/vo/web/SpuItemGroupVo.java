package com.yeye.mall.product.vo.web;

import lombok.Data;

import java.util.List;


@Data
public class SpuItemGroupVo {

  private String groupName;

  private List<SpuBaseAttrVo> attrVos;
}
