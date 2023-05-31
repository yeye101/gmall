package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

