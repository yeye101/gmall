package com.yeye.mall.seckill.to;

import com.yeye.mall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillSkuRedisTo {

  private Long id;
  /**
   * 活动id
   */
  private Long promotionId;
  /**
   * 活动场次id
   */
  private Long promotionSessionId;
  /**
   * 商品id
   */
  private Long skuId;
  /**
   * 秒杀价格
   */
  private BigDecimal seckillPrice;
  /**
   * 秒杀总量
   */
  private BigDecimal seckillCount;
  /**
   * 每人限购数量
   */
  private BigDecimal seckillLimit;
  /**
   * 排序
   */
  private Integer seckillSort;

  // 商品详细信息
  private SkuInfoVo skuInfoVo;

  // 开始时间
  private Long startTime;

  // 结束时间
  private Long endTime;

  private String randomCode;


}
