package com.yeye.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.member.entity.MemberReceiveAddressEntity;
import com.yeye.mall.member.vo.MemberAddressSaveVo;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:36:54
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

  PageUtils queryPage(Map<String, Object> params);

  List<MemberReceiveAddressEntity> getAddressById(Long memberId);

  R memberModify(MemberAddressSaveVo vo);
}

