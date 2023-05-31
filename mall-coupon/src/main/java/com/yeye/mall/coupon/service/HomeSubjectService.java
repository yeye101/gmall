package com.yeye.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.coupon.entity.HomeSubjectEntity;

import java.util.Map;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:03
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

  PageUtils queryPage(Map<String, Object> params);
}

