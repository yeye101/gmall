package com.yeye.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yeye.common.valid.AddGroup;
import com.yeye.common.valid.ListValue;
import com.yeye.common.valid.UpdateGroup;
import com.yeye.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 品牌id
   */
  @NotNull(message = "修改必须指定品牌id", groups = {UpdateGroup.class})
  @Null(message = "新增不能指定id", groups = {AddGroup.class})
  @TableId
  private Long brandId;
  /**
   * 品牌名
   */
  @NotBlank(message = "品牌名必须提交", groups = {AddGroup.class, UpdateGroup.class})
  private String name;
  /**
   * 品牌logo地址
   */
  @NotBlank(groups = {AddGroup.class})
  @URL(message = "logo必须是一个合法的url地址", groups = {AddGroup.class, UpdateGroup.class})
  private String logo;
  /**
   * 介绍
   */
  private String descript;
  /**
   * 显示状态[0-不显示；1-显示]。自定义校验
   */
//	@Pattern()
  @NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
  @ListValue(vals = {0, 1}, groups = {AddGroup.class, UpdateStatusGroup.class})
  private Integer showStatus;
  /**
   * 检索首字母
   */
  @NotEmpty(groups = {AddGroup.class})
  @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
  private String firstLetter;
  /**
   * 排序
   */
  @NotNull(groups = {AddGroup.class})
  @Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
  private Integer sort;

}