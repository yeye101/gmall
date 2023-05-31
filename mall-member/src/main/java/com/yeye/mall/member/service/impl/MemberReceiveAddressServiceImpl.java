package com.yeye.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.common.utils.R;
import com.yeye.mall.member.dao.MemberReceiveAddressDao;
import com.yeye.mall.member.entity.MemberReceiveAddressEntity;
import com.yeye.mall.member.service.MemberReceiveAddressService;
import com.yeye.mall.member.vo.MemberAddressSaveVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<MemberReceiveAddressEntity> page = this.page(
      new Query<MemberReceiveAddressEntity>().getPage(params),
      new QueryWrapper<MemberReceiveAddressEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public List<MemberReceiveAddressEntity> getAddressById(Long memberId) {
    List<MemberReceiveAddressEntity> list = this.
      list(new LambdaQueryWrapper<MemberReceiveAddressEntity>().eq(MemberReceiveAddressEntity::getMemberId, memberId));

    return list;
  }

  @Override
  public R memberModify(MemberAddressSaveVo vo) {

    int count = this.count(new LambdaQueryWrapper<MemberReceiveAddressEntity>()
      .eq(MemberReceiveAddressEntity::getMemberId, vo.getUserId()));

    MemberReceiveAddressEntity entity = new MemberReceiveAddressEntity();
    entity.setMemberId(vo.getUserId());
    entity.setName(vo.getName());
    entity.setPhone(vo.getPhone());
    entity.setProvince(vo.getProvince());
    entity.setDetailAddress(vo.getAddress());
    if (count == 0) {
      entity.setDefaultStatus(1);
    }else {
      entity.setDefaultStatus(0);
    }

    this.save(entity);
    return R.ok();
  }

}