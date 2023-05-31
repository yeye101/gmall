package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yeye.mall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveBatch(List<AttrGroupRelationVo> vos);
}

