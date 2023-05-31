package com.yeye.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.ware.dao.PurchaseDetailDao;
import com.yeye.mall.ware.entity.PurchaseDetailEntity;
import com.yeye.mall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {

    LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();

    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(PurchaseDetailEntity::getId, key)
        .or().eq(PurchaseDetailEntity::getSkuId, key);
    }

    String status = (String) params.get("status");
    if (!StringUtils.isEmpty(status)) {
      wrapper.eq(PurchaseDetailEntity::getStatus, status);
    }

    String wareId = (String) params.get("wareId");
    if (!StringUtils.isEmpty(wareId)) {
      wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
    }

    wrapper.orderByDesc(PurchaseDetailEntity::getId);

    IPage<PurchaseDetailEntity> page = this.page(
      new Query<PurchaseDetailEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);
  }

}