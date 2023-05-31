package com.yeye.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

