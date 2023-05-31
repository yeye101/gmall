package com.yeye.mall.product.vo.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog2Vo {
  //1级id
  private String catalog1Id;
  //子分类
  private List<Catalog3Vo> catalog3List;
  private String id;
  private String name;

}
