package com.yeye.mall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 专题商品
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:03
 */
@Data
@TableName("sms_home_subject_spu")
public class HomeSubjectSpuEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @TableId
  private Long id;
  /**
   * 专题名字
   */
  private String name;
  /**
   * 专题id
   */
  private Long subjectId;
  /**
   * spu_id
   */
  private Long spuId;
  /**
   * 排序
   */
  private Integer sort;

}
