package com.yeye.mall.product.dao;

import com.yeye.mall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {

}
