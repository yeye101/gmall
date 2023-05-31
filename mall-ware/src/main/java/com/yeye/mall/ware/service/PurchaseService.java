package com.yeye.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.ware.entity.PurchaseEntity;
import com.yeye.mall.ware.vo.MergeVo;
import com.yeye.mall.ware.vo.PurchaseFinishVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
public interface PurchaseService extends IService<PurchaseEntity> {

  PageUtils queryPage(Map<String, Object> params);

  PageUtils queryPageUnreceive(Map<String, Object> params);

  void mergePurchase(MergeVo vo);

  void receivedPurchase(List<Long> ids);

  void done(PurchaseFinishVo vo);
}

