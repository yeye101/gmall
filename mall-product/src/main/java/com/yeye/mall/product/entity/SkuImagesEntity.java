package com.yeye.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * sku图片
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@Data
@TableName("pms_sku_images")
public class SkuImagesEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @TableId
  private Long id;
  /**
   * sku_id
   */
  private Long skuId;
  /**
   * 图片地址
   */
  private String imgUrl;
  /**
   * 排序
   */
  private Integer imgSort;
  /**
   * 默认图[0 - 不是默认图，1 - 是默认图]
   */
  private Integer defaultImg;

}
