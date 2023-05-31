package com.yeye.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.to.MemberPrice;
import com.yeye.common.to.SkuReductionTo;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.coupon.dao.SkuFullReductionDao;
import com.yeye.mall.coupon.entity.MemberPriceEntity;
import com.yeye.mall.coupon.entity.SkuFullReductionEntity;
import com.yeye.mall.coupon.entity.SkuLadderEntity;
import com.yeye.mall.coupon.service.MemberPriceService;
import com.yeye.mall.coupon.service.SkuFullReductionService;
import com.yeye.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

  @Autowired
  SkuLadderService skuLadderService;

  @Autowired
  MemberPriceService memberPriceService;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SkuFullReductionEntity> page = this.page(
      new Query<SkuFullReductionEntity>().getPage(params),
      new QueryWrapper<SkuFullReductionEntity>()
    );

    return new PageUtils(page);
  }

  @Transactional
  @Override
  public void saveSkuReduction(SkuReductionTo reductionTo) {

    //1. 保存满减打折
    // sku的优惠满减信息 sms_sku_ladder  sms_sku_full_reduction  sms_member_price
    SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
    skuLadderEntity.setSkuId(reductionTo.getSkuId());
    skuLadderEntity.setFullCount(reductionTo.getFullCount());
    skuLadderEntity.setDiscount(reductionTo.getDiscount());
    skuLadderEntity.setAddOther(reductionTo.getCountStatus());

    if (reductionTo.getDiscount().compareTo(new BigDecimal("0")) == 1) {
      skuLadderService.save(skuLadderEntity);
    }


    //  sms_sku_full_reduction
    SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
    BeanUtils.copyProperties(reductionTo, reductionEntity);


    if (reductionTo.getReducePrice().compareTo(new BigDecimal("0")) == 1) {
      this.save(reductionEntity);
    }

    // sms_member_price
    List<MemberPrice> memberPrice = reductionTo.getMemberPrice();

    List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
      MemberPriceEntity priceEntity = new MemberPriceEntity();
      priceEntity.setSkuId(reductionTo.getSkuId());
      priceEntity.setMemberLevelId(item.getId());
      priceEntity.setMemberLevelName(item.getName());
      priceEntity.setMemberPrice(item.getPrice());

      return priceEntity;
    }).filter(entity -> {
      return entity.getMemberPrice().compareTo(new BigDecimal("0")) == 1;
    }).collect(Collectors.toList());


    memberPriceService.saveBatch(collect);


  }

}