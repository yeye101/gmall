package com.yeye.mall.search.service.impl;

import cn.hutool.core.util.PageUtil;
import com.alibaba.fastjson.JSON;
import com.yeye.common.to.es.SkuEsModel;
import com.yeye.mall.search.config.ElasticSearchConfig;
import com.yeye.mall.search.constant.EsConstant;
import com.yeye.mall.search.feign.ProductFeignService;
import com.yeye.mall.search.result.SearchResult;
import com.yeye.mall.search.service.MallSearchService;
import com.yeye.mall.search.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("mallSearchService")
public class MallSearchServiceImpl implements MallSearchService {


  @Autowired
  RestHighLevelClient client;

  @Autowired
  ProductFeignService productFeignService;

  @Override
  public SearchResult search(SearchVo searchVo) {

    SearchResult result = new SearchResult();

    SearchRequest request = getSearchRequest(searchVo);


    try {


      SearchResponse response = client.search(request, ElasticSearchConfig.COMMON_OPTIONS);
      result = getSearchResult(response, searchVo);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }

  private SearchResult getSearchResult(SearchResponse param, SearchVo vo) {
    SearchResult result = new SearchResult();


    List<SkuEsModel> products = new ArrayList<>();
    SearchHits searchHits = param.getHits();
    // 拿到商品
    if (searchHits.getHits() != null && searchHits.getHits().length > 0) {
      for (SearchHit hit : searchHits.getHits()) {
        String sourceAsString = hit.getSourceAsString();
        SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);

        if (StringUtils.isNotEmpty(vo.getKeyword())) {
          HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
          Text[] fragments = skuTitle.getFragments();
          String highlightSkuTitle = fragments[0].string();
          skuEsModel.setSkuTitle(highlightSkuTitle);
        }

        products.add(skuEsModel);
      }
    }
    result.setProducts(products);


    List<BrandVo> brandVos = new ArrayList<>();//品牌属性

    List<AttrVo> attrVos = new ArrayList<>();//基础属性

    List<CatalogVo> catalogVos = new ArrayList<>();//分类属性

    Aggregations aggregations = param.getAggregations();
    // 品牌
    Terms brandAgg = aggregations.get("brandAgg");
    List<? extends Terms.Bucket> brandAggBuckets = brandAgg.getBuckets();
    for (Terms.Bucket bucket : brandAggBuckets) {
      BrandVo brandVo = new BrandVo();
      String brandId = bucket.getKeyAsString();
      brandVo.setBrandId(Long.parseLong(brandId));
      // 子聚合
      Aggregations bucketAggregations = bucket.getAggregations();

      Terms brandNameAgg = bucketAggregations.get("brandNameAgg");
      brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());

      Terms brandImgAgg = bucketAggregations.get("brandImgAgg");
      brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
      // 加到list
      brandVos.add(brandVo);
    }

    Terms catalogAgg = aggregations.get("catalogAgg");
    List<? extends Terms.Bucket> catalogAggBuckets = catalogAgg.getBuckets();
    for (Terms.Bucket bucket : catalogAggBuckets) {
      CatalogVo catalogVo = new CatalogVo();
      // 取到id
      String catalogId = bucket.getKeyAsString();
      catalogVo.setCatalogId(Long.parseLong(catalogId));
      // 子聚合
      Aggregations bucketAggregations = bucket.getAggregations();
      Terms catalogNameAgg = bucketAggregations.get("catalogNameAgg");
      catalogVo.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
      // 加到list
      catalogVos.add(catalogVo);
    }


    Nested attrsAgg = aggregations.get("attrsAgg");
    Terms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
    for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
      AttrVo attrVo = new AttrVo();

      String attrId = bucket.getKeyAsString();
      attrVo.setAttrId(Long.parseLong(attrId));

      Aggregations bucketAggregations = bucket.getAggregations();
      Terms attrNameAgg = bucketAggregations.get("attrNameAgg");
      attrVo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());

      Terms attrValueAgg = bucketAggregations.get("attrValueAgg");
      List<String> attrValues = attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
      attrVo.setAttrValue(attrValues);
      attrVos.add(attrVo);
    }


    result.setAttrVos(attrVos);
    result.setBrandVos(brandVos);
    result.setCatalogVos(catalogVos);


    // 分页
    int pageNum = vo.getPageNum() == null ? 1 : vo.getPageNum();

    Long total = searchHits.getTotalHits().value;
    Integer totalPages = PageUtil.totalPage(total.intValue(), EsConstant.PAGE_SIZE);
    int[] rainbow = PageUtil.rainbow(pageNum, total.intValue(), EsConstant.PAGE_SHOW_SIZE);
    result.setTotal(total);
    result.setTotalPages(totalPages);
    result.setPageNum(pageNum);
    result.setRainBow(rainbow);




/*    // 构建面包屑
    List<NavVo> navVos = new ArrayList<>();
    if (!CollectionUtils.isEmpty(attrVos)) {

      navVos = attrVos.stream().map(item -> {
        NavVo navVo = new NavVo();
        navVo.setNavName(item.getAttrName());
        navVo.setNavValue(String.join(":", item.getAttrValue()));

        if (StringUtils.isNotEmpty(vo.get_queryString())) {
          String queryString = vo.get_queryString();

          String attr = item.getAttrId() + "_" + String.join(":", item.getAttrValue());

          String encode = null;
          try {
            encode = URLEncoder.encode(attr, "UTF-8");
          } catch (Exception e) {
            e.printStackTrace();
          }
          String replace = queryString.replace("&attrs=" + encode, "")
            .replace("attrs=" + attr + "&", "")
            .replace("attrs=" + attr, "");

          navVo.setLink("http://search.mall-yeye.com/search.html" + (replace.isEmpty() ? "" : "?" + replace));
        }
        return navVo;
      }).collect(Collectors.toList());
    }*/

    Map<Long, String> attrNames = new HashMap<>();
    if (!CollectionUtils.isEmpty(attrVos)) {
      attrNames = attrVos.stream().collect(Collectors.toMap(AttrVo::getAttrId, AttrVo::getAttrName));
    }

    Map<Long, String> brandNames = new HashMap<>();
    if (!CollectionUtils.isEmpty(attrVos)) {
      brandNames = brandVos.stream().collect(Collectors.toMap(BrandVo::getBrandId, BrandVo::getBrandName));
    }


    Map<Long, String> catalogNames = new HashMap<>();
    if (!CollectionUtils.isEmpty(attrVos)) {
      catalogNames = catalogVos.stream().collect(Collectors.toMap(CatalogVo::getCatalogId, CatalogVo::getCatalogName));
    }

    List<NavVo> navVos = new ArrayList<>();

    if (!CollectionUtils.isEmpty(vo.getBrandId())) {
      Map<Long, String> finalBrandNames = brandNames;
      List<NavVo> collect = vo.getBrandId().stream().map(item -> {
        NavVo navVo = new NavVo();
        navVo.setNavName("品牌");
        if (finalBrandNames.containsKey(item)) {
          navVo.setNavName(finalBrandNames.get(item));
        }

        String replace = getQueryString(vo, String.valueOf(item), "brandId");
        navVo.setLink("http://search.mall-yeye.com/list.html" + (replace.isEmpty() ? "" : "?" + replace));

        return navVo;
      }).collect(Collectors.toList());

      navVos.addAll(collect);
    }

    Long catalog3Id = vo.getCatalog3Id();
    if (catalog3Id != null) {
      NavVo navVo = new NavVo();
      navVo.setNavName("分类");
      if (catalogNames.containsKey(catalog3Id)) {
        navVo.setNavName(catalogNames.get(catalog3Id));
      }

      String replace = getQueryString(vo, String.valueOf(catalog3Id), "catalog3Id");
      navVo.setLink("http://search.mall-yeye.com/list.html" + (replace.isEmpty() ? "" : "?" + replace));

      navVos.add(navVo);
    }


    List<Long> attrNavIds = new ArrayList<>();
    // 6. 构建面包屑导航
    List<String> attrs = vo.getAttrs();
    if (attrs != null && attrs.size() > 0) {
      Map<Long, String> finalAttrNames = attrNames;
      List<NavVo> navVoList = attrs.stream().map(attr -> {
        NavVo navVo = new NavVo();

        String[] split = attr.split("_");
        String attrId = split[0];

        //6.1 设置属性值
        navVo.setNavValue(split[1]);
        attrNavIds.add(Long.parseLong(attrId));
        //6.2 查询并设置属性名
        if (finalAttrNames.containsKey(Long.parseLong(attrId))) {
          navVo.setNavName(finalAttrNames.get(Long.parseLong(attrId)));
        }

        String replace = getQueryString(vo, attr, "attrs");

        navVo.setLink("http://search.mall-yeye.com/list.html" + (replace.isEmpty() ? "" : "?" + replace));
        return navVo;
      }).collect(Collectors.toList());
      navVos.addAll(navVoList);
    }

    result.setAttrNavIds(attrNavIds);
    result.setNavs(navVos);

    return result;
  }

  private String getQueryString(SearchVo vo, String value, String replace) {
    String encode = null;
    try {
      encode = URLEncoder.encode(value, "UTF-8");
      encode = encode.replace("+", "%20");
      encode = encode.replace("%28", "(");
      encode = encode.replace("%29", ")");

    } catch (Exception e) {
      e.printStackTrace();
    }
    //6.3 设置面包屑跳转链接(当点击该链接时剔除点击属性)
    String queryString = vo.get_queryString();
    return queryString.replace("&" + replace + "=" + encode, "")
      .replace(replace + "=" + encode + "&", "")
      .replace(replace + "=" + encode, "");
  }

  private SearchRequest getSearchRequest(SearchVo searchVo) {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

    // 查询

    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    // must的getKeyword
    if (StringUtils.isNotEmpty(searchVo.getKeyword())) {
      boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchVo.getKeyword()));
    }
    // filter的getCatalog3Id
    if (searchVo.getCatalog3Id() != null) {
      boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchVo.getCatalog3Id()));
    }
    // filter的getBrandId
    if (!CollectionUtils.isEmpty(searchVo.getBrandId())) {
      boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchVo.getBrandId()));
    }

    // filter的hasStock
    if (searchVo.getHasStock() != null) {
      boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchVo.getHasStock() == 1 ? Boolean.TRUE : Boolean.FALSE));
    }

    //range的价格区间 1_500/_5000/5000_

    if (StringUtils.isNotEmpty(searchVo.getSkuPrice()) && !"_".equals(searchVo.getSkuPrice())) {
      RangeQueryBuilder skuPriceRange = QueryBuilders.rangeQuery("skuPrice");

      String[] s = searchVo.getSkuPrice().split("_");

      if (searchVo.getSkuPrice().startsWith("_")) {
        skuPriceRange.lte(s[1]);
      } else if (searchVo.getSkuPrice().endsWith("_")) {
        skuPriceRange.gte(s[0]);
      } else if (s.length == 2) {
        skuPriceRange.gte(s[0]).lte(s[1]);
      }

      boolQueryBuilder.filter(skuPriceRange);

    }

    // filter的getAttrs  attr = 1_15:50
    if (!CollectionUtils.isEmpty(searchVo.getAttrs())) {
      for (String attr : searchVo.getAttrs()) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 第一位为id
        String[] s_id = attr.split("_");
        // 第二位为值
        String[] values = s_id[1].split(":");

        boolQuery.must(QueryBuilders.termQuery("attrs.attrId", s_id[0]));

        boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", values));

        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);

        boolQueryBuilder.filter(nestedQueryBuilder);
      }
    }


    sourceBuilder.query(boolQueryBuilder);
    // 排序等 skuPrice_asc/desc
    if (StringUtils.isNotEmpty(searchVo.getSort())) {
      String sort = searchVo.getSort();

      String[] split = sort.split("_");
      sourceBuilder.sort(split[0], "asc".equalsIgnoreCase(split[1]) ? SortOrder.ASC : SortOrder.DESC);
    }

    // 分页
    sourceBuilder.from(searchVo.getPageNum() == null ? 0 : (searchVo.getPageNum() - 1) * EsConstant.PAGE_SIZE);
    sourceBuilder.size(EsConstant.PAGE_SIZE);

    // 高亮
    if (StringUtils.isNotEmpty(searchVo.getKeyword())) {
      HighlightBuilder highlightBuilder = new HighlightBuilder();
      highlightBuilder.field("skuTitle");
      highlightBuilder.preTags("<b style= 'color:red'>");
      highlightBuilder.postTags("</b>");

      sourceBuilder.highlighter(highlightBuilder);
    }


    // 聚合条件
    // 品牌聚合
    TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg");
    brandAgg.field("brandId").size(10);
    brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
    brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));

    sourceBuilder.aggregation(brandAgg);

    // 分类聚合
    TermsAggregationBuilder catelogAgg = AggregationBuilders.terms("catalogAgg");
    catelogAgg.field("catalogId").size(10);
    catelogAgg.subAggregation(AggregationBuilders.terms("catalogNameAgg").field("catalogName").size(1));

    sourceBuilder.aggregation(catelogAgg);

    // 属性
    NestedAggregationBuilder nested = AggregationBuilders.nested("attrsAgg", "attrs");
    TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(10);
    nested.subAggregation(attrIdAgg);
    attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
    attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(10));

    sourceBuilder.aggregation(nested);


    System.out.println(sourceBuilder.toString());

    SearchRequest request = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);

    return request;
  }

/**
 * json工具查看
 * GET product/_search
 * {"query":{"bool":{"must":[{"match":{"skuTitle":"华为"}}],"filter":[{"term":{"catalogId":"225"}},{"term":{"hasStock":false}},{"terms":{"brandId":[9,10]}},{"range":{"skuPrice":{"gte":0,"lte":6000}}},{"nested":{"path":"attrs","query":{"bool":{"must":[{"term":{"attrs.attrId":{"value":15}}},{"terms":{"attrs.attrValue":["海思（Hisilicon）","HUAWEI Kirin 980"]}}]}}}}]}},"aggs":{"brandAgg":{"terms":{"field":"brandId","size":10},"aggs":{"brandNameAgg":{"terms":{"field":"brandName","size":10}},"brandImgAgg":{"terms":{"field":"brandImg","size":10}}}},"catalogAgg":{"terms":{"field":"catalogId","size":10},"aggs":{"catalogNameAgg":{"terms":{"field":"catalogName","size":10}}}},"attrsAgg":{"nested":{"path":"attrs"},"aggs":{"attrIdAgg":{"terms":{"field":"attrs.attrId","size":10},"aggs":{"attrNameAgg":{"terms":{"field":"attrs.attrName","size":10}},"attrValueAgg":{"terms":{"field":"attrs.attrValue","size":10}}}}}}},"sort":[{"skuPrice":{"order":"desc"}}],"highlight":{"fields":{"skuTitle":{}},"pre_tags":"<b style= 'color:red'>","post_tags":"</b>"},"from":4,"size":2}
 */

}
