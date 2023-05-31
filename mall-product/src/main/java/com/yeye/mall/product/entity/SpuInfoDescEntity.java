package com.yeye.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * spu信息介绍
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 商品id
   */
  @TableId(type = IdType.INPUT)
  private Long spuId;
  /**
   * 商品介绍
   */
  private String decript;

}
