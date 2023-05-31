package com.yeye.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

