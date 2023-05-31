package com.yeye.mall.cart.vo;

import lombok.Data;

@Data
public class UserInfoTo {

  private Long userId;

  private String userKey;

  private Boolean tempUser = Boolean.FALSE;


}
