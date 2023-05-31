package com.yeye.mall.product.dao;

import com.yeye.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeye.mall.product.vo.web.SpuItemGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

  List<SpuItemGroupVo> getGroupAttrsBySIdAndCId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
