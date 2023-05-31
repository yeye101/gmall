package com.yeye.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:24
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

