package com.yeye.mall.member.vo;

import com.yeye.mall.member.entity.MemberEntity;
import com.yeye.mall.member.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.util.List;

@Data
public class MemberInfoVo extends MemberEntity {

  // 会员地址列表
  private List<MemberReceiveAddressEntity> addressVos;


}
