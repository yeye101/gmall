package com.yeye.mall.gateway.config;

import cn.hutool.core.text.AntPathMatcher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class MallFilterConfiguration implements GlobalFilter {


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpResponse response = exchange.getResponse();
    ServerHttpRequest request = exchange.getRequest();

    String uri = request.getURI().getPath();
    boolean match = new AntPathMatcher().match("/api/**", uri);
    if (match) {
      ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(header -> {
        header.add("source", "dashboard");
      }).build();
      //把新的 exchange放回到过滤链
      exchange.mutate().request(serverHttpRequest);
    }


    return chain.filter(exchange);
  }
}
