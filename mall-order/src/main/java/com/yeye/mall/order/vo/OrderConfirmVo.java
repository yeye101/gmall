package com.yeye.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页vo
 */
@Data
public class OrderConfirmVo {

  // 会员地址列表
  private List<MemberAddressVo> addressVos;

  // 所有选中的购物项
  private List<OrderItemVo> orderItemVos;
  private Integer orderItemCount;

  private String orderToken;

  // 发票暂无

  // 优惠卷信息
  private Integer integration;

  // 订单总额
  private BigDecimal total;

  // 应付金额
  private BigDecimal payPrice;



}
