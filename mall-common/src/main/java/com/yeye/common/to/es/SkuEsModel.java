package com.yeye.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEsModel {
  private Long skuId;

  private Long spuId;

  private String skuTitle;

  private BigDecimal skuPrice;

  private String skuImg;

  private Long saleCount;

  private Boolean hasStock;

  private Long hotScore;


  private Long catalogId;

  private String catalogName;

  private Long brandId;

  private String brandName;

  private String brandImg;


  private List<Attrs> attrs;

}
