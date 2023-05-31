package com.yeye.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:36:54
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

