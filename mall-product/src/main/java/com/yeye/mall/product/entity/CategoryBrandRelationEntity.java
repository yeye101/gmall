package com.yeye.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 品牌分类关联
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:43
 */
@Data
@TableName("pms_category_brand_relation")
public class CategoryBrandRelationEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   *
   */
  @TableId
  private Long id;
  /**
   * 品牌id
   */
  private Long brandId;
  /**
   * 分类id
   */
  private Long catelogId;
  /**
   *
   */
  private String brandName;
  /**
   *
   */
  private String catelogName;

}
