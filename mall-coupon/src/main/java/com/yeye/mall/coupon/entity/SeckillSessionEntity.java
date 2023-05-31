package com.yeye.mall.coupon.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 秒杀活动场次
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:03
 */
@Data
@TableName("sms_seckill_session")
public class SeckillSessionEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @TableId
  private Long id;
  /**
   * 场次名称
   */
  private String name;
  /**
   * 每日开始时间
   */
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;
  /**
   * 每日结束时间
   */
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date endTime;
  /**
   * 启用状态
   */
  private Integer status;
  /**
   * 创建时间
   */
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  @TableField(exist = false)
  private List<SeckillSkuRelationEntity> list;

}
