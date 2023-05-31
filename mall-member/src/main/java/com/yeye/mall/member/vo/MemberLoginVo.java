package com.yeye.mall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MemberLoginVo {

  private String account;

  private String password;

}
