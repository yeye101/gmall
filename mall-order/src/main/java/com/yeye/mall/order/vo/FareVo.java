package com.yeye.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
  private MemberAddressVo addressVo;

  private BigDecimal fare;
}
