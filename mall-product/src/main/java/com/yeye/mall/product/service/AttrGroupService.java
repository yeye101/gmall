package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.AttrGroupEntity;
import com.yeye.mall.product.vo.AttrGroupWithAttrsVo;
import com.yeye.mall.product.vo.web.SpuItemGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

  PageUtils queryPage(Map<String, Object> params);

  PageUtils queryPage(Map<String, Object> params, Long catelogId);

  List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

  List<SpuItemGroupVo> getGroupAttrsBySIdAndCId(Long spuId, Long catalogId);
}

