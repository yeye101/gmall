package com.yeye.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

  public static final RequestOptions COMMON_OPTIONS;

  static {
    RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
    /* 加入请求头*/
//    builder.addHeader("Authorizetion","Bearer" + TOKEN);
    COMMON_OPTIONS = builder.build();
  }

  @Bean
  public RestHighLevelClient esRestClient() {
    RestHighLevelClient client = new RestHighLevelClient(
      RestClient.builder(
        //在这里配置你的elasticsearch的情况
        new HttpHost("127.0.0.1", 9200, "http") //todo 写自己的es地址
      )
    );
    return client;
  }
}
