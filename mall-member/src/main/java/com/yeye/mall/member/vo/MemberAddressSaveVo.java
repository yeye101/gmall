package com.yeye.mall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class MemberAddressSaveVo {

  private Long userId;

  @Length(min = 1, max = 20, message = "用户名长度必须在1-20之间")
  @NotEmpty(message = "用户名必须提交")
  private String name;

  @NotEmpty(message = "手机号不能为空")
  @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
  private String phone;

  @Length(min = 1, max = 20, message = "省份长度必须在1-20之间")
  @NotEmpty(message = "省份必须提交")
  private String province;

  @Length(min = 1, max = 30, message = "地址信息长度必须在1-20之间")
  @NotEmpty(message = "地址信息必须提交")
  private String address;

}
