package com.yeye.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {
  private Long skuId;

  private String title;

  private String image;

  private List<String> skuAttr;

  private BigDecimal price;

  private Integer count;

  private BigDecimal totalPrice;

  private Boolean stock = Boolean.FALSE;

}
