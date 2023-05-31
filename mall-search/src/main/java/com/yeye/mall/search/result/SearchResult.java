package com.yeye.mall.search.result;

import com.yeye.common.to.es.SkuEsModel;
import com.yeye.mall.search.vo.AttrVo;
import com.yeye.mall.search.vo.BrandVo;
import com.yeye.mall.search.vo.CatalogVo;
import com.yeye.mall.search.vo.NavVo;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

  // es中的商品数据
  private List<SkuEsModel> products;

  // 分页信息
  private Integer pageNum;
  private Long total;
  private Integer totalPages;
  private int[] rainBow;

  private List<BrandVo> brandVos;//品牌属性

  private List<AttrVo> attrVos;//基础属性

  private List<CatalogVo> catalogVos;//分类属性

  private List<NavVo> navs; // 面包屑

  private List<Long> attrNavIds;
}
