package com.yeye.mall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 库存工作单
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity implements Serializable {
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
   * sku_name
   */
  private String skuName;
  /**
   * 购买个数
   */
  private Integer skuNum;
  /**
   * 工作单id
   */
  private Long taskId;
  /**
   * 仓库id
   */
  private Long wareId;
  /**
   *  1-锁定 2-解锁 3-扣减
   */
  private Integer lockStatus;

}
