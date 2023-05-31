package com.yeye.mall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginVo {

  @NotEmpty(message = "账号必须提交")
  private String account;

  @NotEmpty(message = "密码必须提交")
  private String password;

}
