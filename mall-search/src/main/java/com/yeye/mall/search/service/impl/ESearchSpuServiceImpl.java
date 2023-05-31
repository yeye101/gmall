package com.yeye.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.yeye.common.exception.BizCodeEnum;
import com.yeye.common.to.es.SkuEsModel;
import com.yeye.common.utils.R;
import com.yeye.mall.search.config.ElasticSearchConfig;
import com.yeye.mall.search.constant.EsConstant;
import com.yeye.mall.search.service.ESearchSpuService;
import com.yeye.mall.search.vo.BrandVo;
import com.yeye.mall.search.vo.CatalogVo;
import com.yeye.mall.search.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentSubParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("eSearchSaveService")
public class ESearchSpuServiceImpl implements ESearchSpuService {

  @Autowired
  RestHighLevelClient client;

  @Override
  public Boolean upProduct(List<SkuEsModel> skuEsModels) {

    BulkRequest bulkRequest = new BulkRequest();
    for (SkuEsModel skuEsModel : skuEsModels) {
      IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
      indexRequest.id(skuEsModel.getSkuId().toString());
      String skuJson = JSON.toJSONString(skuEsModel);
      indexRequest.source(skuJson, XContentType.JSON);

      bulkRequest.add(indexRequest);
    }

    try {
      BulkResponse bulk = client.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);

      if (bulk.hasFailures()) {
        List<String> list = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
        log.error("商品上架错误,ids={}", list);

        return Boolean.FALSE;
      }
    } catch (IOException e) {
      log.error("upProductToEs商品上架错误,e=", e);
    }

    return Boolean.TRUE;


  }

  @Override
  public Boolean downProduct(Long spuId) {

    DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(EsConstant.PRODUCT_INDEX);
    deleteByQueryRequest.setQuery(new TermQueryBuilder("spuId", spuId));

    try {
      BulkByScrollResponse bulk = client.deleteByQuery(deleteByQueryRequest, ElasticSearchConfig.COMMON_OPTIONS);

    } catch (IOException e) {
      log.error("downProduct商品下架错误,e=", e);
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }

  @Override
  public R getSpuProduct(Long catelogId) {
    List<SkuEsModel> data = new ArrayList<>();

    SearchVo searchVo = new SearchVo();
    searchVo.setCatalog3Id(catelogId);
    SearchRequest searchRequest = getSearchRequest(searchVo);
    try {
      SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
      getSearchResult(response, data);
    } catch (IOException e) {
      e.printStackTrace();
      return R.error(BizCodeEnum.PRODUCT_SKU_LIST_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_SKU_LIST_EXCEPTION.getMsg());

    }

    return R.ok().put("data", data);
  }

  private void getSearchResult(SearchResponse response, List<SkuEsModel> data) {

    Aggregations aggregations = response.getAggregations();
    // 品牌
    Terms spuAgg = aggregations.get("spuAgg");
    List<? extends Terms.Bucket> spuAggBuckets = spuAgg.getBuckets();
    for (Terms.Bucket bucket : spuAggBuckets) {
      SkuEsModel skuEsModel = new SkuEsModel();
      // 子聚合
      Aggregations bucketAggregations = bucket.getAggregations();

      TopHits topHits = bucketAggregations.get("top1Agg");

      for (SearchHit hit : topHits.getHits()) {
        skuEsModel = JSON.parseObject(hit.getSourceAsString(), SkuEsModel.class);
        data.add(skuEsModel);
      }


    }


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


    sourceBuilder.query(boolQueryBuilder);

    // 分页
    sourceBuilder.size(EsConstant.PAGE_SIZE);


    // 聚合条件
    // 品牌聚合
    TermsAggregationBuilder spuAgg = AggregationBuilders.terms("spuAgg");
    spuAgg.field("spuId").size(7).minDocCount(1);
    TopHitsAggregationBuilder top1Agg = AggregationBuilders.topHits("top1Agg").sort("skuPrice", SortOrder.DESC).size(1);
    spuAgg.subAggregation(top1Agg);

    sourceBuilder.aggregation(spuAgg);


    System.out.println(sourceBuilder.toString());

    SearchRequest request = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);

    return request;
  }
}
