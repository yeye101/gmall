package com.yeye.mall.search;

import com.alibaba.fastjson.JSON;
import com.yeye.mall.search.config.ElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchApplicationTests {

  @Autowired
  private RestHighLevelClient client;

  @Test
  public void initData() throws IOException {
    IndexRequest indexRequest = new IndexRequest("user");
    indexRequest.id("1");
    indexRequest.source("age", 18,
      "name", "张三");
    IndexResponse index = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
    System.out.println(index);
  }

  @Test
  public void searchData() throws IOException {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices("bank");

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.from(0);
    sourceBuilder.size(20);
    sourceBuilder.query(QueryBuilders.matchAllQuery());


    // 聚合，计算年龄分布
    sourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age").size(10));

    // 聚合，计算月薪平均
    sourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));

    System.out.println(sourceBuilder.toString());
    searchRequest.source(sourceBuilder);

    SearchResponse search = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
//    System.out.println(search.toString());
    // 获取数据
    SearchHit[] hits = search.getHits().getHits();
    for (SearchHit hit : hits) {
      String string = hit.getSourceAsString();
      account account = JSON.parseObject(string, account.class);
      System.out.println(account);
    }
    Aggregations aggregations = search.getAggregations();
    Terms ageAgg = aggregations.get("ageAgg");
    for (Terms.Bucket bucket : ageAgg.getBuckets()) {
      System.out.println("年龄分布"+bucket.getKeyAsString());
    }
    Avg balanceAvg = aggregations.get("balanceAvg");
    System.out.println(balanceAvg.getValue());


  }

  @Test
  public void contextLoads() {
    String[] s = "1_500".split("_");
    for (String s1 : s) {
      System.out.println(s1);
    }
  }






  @ToString
  @Data
  static class account {
    private int account_number;
    private int balance;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String address;
    private String employer;
    private String email;
    private String city;
    private String state;
  }

}
