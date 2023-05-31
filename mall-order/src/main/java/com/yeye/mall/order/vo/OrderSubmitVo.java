package com.yeye.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {

  private Long addrId;
  private Integer payType;
  // 无需提交商品 购物车获取

  // 优惠

  // 防重令牌
  private String orderToken;

  private BigDecimal payPrice;

  private String note;


}
