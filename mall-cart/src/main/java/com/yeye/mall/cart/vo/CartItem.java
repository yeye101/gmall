package com.yeye.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItem {
  private Long skuId;

  private Boolean checked = Boolean.TRUE;

  private String title;

  private String image;

  private List<String> skuAttr;

  private BigDecimal price;

  private Integer count;

  private BigDecimal totalPrice;

}
