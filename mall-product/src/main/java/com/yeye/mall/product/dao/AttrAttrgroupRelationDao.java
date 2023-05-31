package com.yeye.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeye.mall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

  void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> entities);
}
