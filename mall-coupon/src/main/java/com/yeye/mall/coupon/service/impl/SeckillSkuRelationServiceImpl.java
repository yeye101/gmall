package com.yeye.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.coupon.dao.SeckillSkuRelationDao;
import com.yeye.mall.coupon.entity.SeckillSkuRelationEntity;
import com.yeye.mall.coupon.service.SeckillSkuRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {

    LambdaQueryWrapper<SeckillSkuRelationEntity> queryWrapper = new LambdaQueryWrapper<SeckillSkuRelationEntity>();


    String promotionSessionId = (String) params.get("promotionSessionId");
    if (StringUtils.isNotBlank(promotionSessionId)) {
      queryWrapper.eq(SeckillSkuRelationEntity::getPromotionSessionId, promotionSessionId);
    }


    IPage<SeckillSkuRelationEntity> page = this.page(
      new Query<SeckillSkuRelationEntity>().getPage(params),
      queryWrapper
    );

    return new PageUtils(page);
  }

}