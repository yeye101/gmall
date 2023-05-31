package com.yeye.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:36:54
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

