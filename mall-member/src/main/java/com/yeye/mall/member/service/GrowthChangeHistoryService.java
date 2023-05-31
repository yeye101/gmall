package com.yeye.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:36:54
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

