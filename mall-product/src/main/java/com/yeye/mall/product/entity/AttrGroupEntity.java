package com.yeye.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 属性分组
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 分组id
   */
  @TableId
  private Long attrGroupId;
  /**
   * 组名
   */
  private String attrGroupName;
  /**
   * 排序
   */
  private Integer sort;
  /**
   * 描述
   */
  private String descript;
  /**
   * 组图标
   */
  private String icon;
  /**
   * 所属分类id
   */
  private Long catelogId;

  @TableField(exist = false)
  private Long[] catelogPath;
}
