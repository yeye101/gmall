package com.yeye.mall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuStock {

  private Long skuId;
  private Integer num;
  private List<Long> wareId;

}
