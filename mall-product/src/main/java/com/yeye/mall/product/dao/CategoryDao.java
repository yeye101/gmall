package com.yeye.mall.product.dao;

import com.yeye.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
