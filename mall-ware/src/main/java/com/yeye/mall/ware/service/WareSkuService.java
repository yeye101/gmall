package com.yeye.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.to.mq.OrderTo;
import com.yeye.common.to.mq.StockLockTo;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.ware.entity.WareSkuEntity;
import com.yeye.mall.ware.vo.SkuHasStockVo;
import com.yeye.mall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
public interface WareSkuService extends IService<WareSkuEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void addStock(Long skuId, Long wareId, Integer skuNum);

  List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

  R lockOrder(WareSkuLockVo vo);

  void handleStockLockedRelease(StockLockTo content);

  void handleOrderCloseRelease(OrderTo orderTo);
}

