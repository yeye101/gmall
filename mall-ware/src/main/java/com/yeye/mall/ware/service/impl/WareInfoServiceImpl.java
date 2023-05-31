package com.yeye.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.common.utils.R;
import com.yeye.mall.ware.dao.WareInfoDao;
import com.yeye.mall.ware.entity.WareInfoEntity;
import com.yeye.mall.ware.feign.MemberFeignService;
import com.yeye.mall.ware.service.WareInfoService;
import com.yeye.mall.ware.vo.FareVo;
import com.yeye.mall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

  @Autowired
  private MemberFeignService memberFeignService;


  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper<>();
    String key = (String) params.get("key");

    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(WareInfoEntity::getId, key)
        .or().like(WareInfoEntity::getName, key)
        .or().like(WareInfoEntity::getAddress, key)
        .or().like(WareInfoEntity::getAreacode, key);
    }

    IPage<WareInfoEntity> page = this.page(
      new Query<WareInfoEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);
  }

  @Override
  public FareVo getFare(Long addrId) {

    FareVo fareVo = new FareVo();
    if (Objects.isNull(addrId)) {



      return fareVo;
    }

    R info = memberFeignService.info(addrId);

    if (info.getCode() != 0){
      return fareVo;
    }
    MemberAddressVo receiveAddress = info.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
    });

    BigDecimal fare = BigDecimal.ONE;
    fareVo.setFare(fare);
    fareVo.setAddressVo(receiveAddress);

    return fareVo;
  }

}