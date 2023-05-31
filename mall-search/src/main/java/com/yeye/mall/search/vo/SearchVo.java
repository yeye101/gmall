package com.yeye.mall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchVo {

  // 标题，全文匹配
  private String keyword;

  // 三级分类id
  private Long catalog3Id;

  // 排序
  private String sort;

  private Integer hasStock;

  private String skuPrice;

  private List<Long> brandId;

  private List<String> attrs;

  private Integer pageNum;

  private String _queryString;

}
