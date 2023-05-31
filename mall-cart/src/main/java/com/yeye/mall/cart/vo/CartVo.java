package com.yeye.mall.cart.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {

  private List<CartItem> cartItems;

  private Integer countNum = 0;

  private Integer countType = 0;

  private BigDecimal totalAmountPrice;

  private BigDecimal reduce;


}
