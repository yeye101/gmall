package com.yeye.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

  PageUtils queryPage(Map<String, Object> params);

  WareOrderTaskEntity getOrderTaskEntityBySn(String orderSn);
}

