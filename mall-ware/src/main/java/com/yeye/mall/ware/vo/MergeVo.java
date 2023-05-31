package com.yeye.mall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
  /**
   * {
   *   purchaseId: 1, //整单id 可以没有
   *   items:[1,2,3,4] //合并项集合
   * }
   */
  private Long purchaseId;
  private List<Long> items;
}
