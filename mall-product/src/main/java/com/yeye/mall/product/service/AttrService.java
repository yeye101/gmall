package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.AttrEntity;
import com.yeye.mall.product.vo.AttrGroupRelationVo;
import com.yeye.mall.product.vo.AttrRespVo;
import com.yeye.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface AttrService extends IService<AttrEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveAttr(AttrVo attr);

  PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

  AttrRespVo getAttrInfo(Long attrId);

  void updateAttr(AttrVo attr);

  List<AttrEntity> getRelationAttr(Long attrgroupId);

  void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVo);

  PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

  List<Long> selectSearchAttrIds();
}

