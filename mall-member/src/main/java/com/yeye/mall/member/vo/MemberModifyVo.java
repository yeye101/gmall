package com.yeye.mall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class MemberModifyVo {

  @Length(min = 1, max = 20, message = "用户名长度必须在1-20之间")
  @NotEmpty(message = "用户名必须提交")
  private String nickName;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date birth;

  private String sign;

}
