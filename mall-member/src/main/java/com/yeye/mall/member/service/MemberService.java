package com.yeye.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.member.entity.MemberEntity;
import com.yeye.mall.member.vo.*;

import java.util.Map;

/**
 * 会员
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:36:54
 */
public interface MemberService extends IService<MemberEntity> {

  PageUtils queryPage(Map<String, Object> params);

  R register(MemberRegisterVo vo);

  void checkUniqueUserByName(MemberRegisterVo vo);

  void checkUniqueUserByPhone(MemberRegisterVo vo);

  R login(MemberLoginVo vo);

  R socialLogin(MemberSocialLoginVo vo);

  R memberModify(MemberModifyVo vo);

  MemberInfoVo getMemberInfo(Long id);
}

