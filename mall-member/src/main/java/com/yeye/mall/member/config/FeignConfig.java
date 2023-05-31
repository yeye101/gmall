package com.yeye.mall.member.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

  @Bean
  public RequestInterceptor requestInterceptor(){
    RequestInterceptor requestInterceptor = requestTemplate -> {
      // 1.拿到进来的请求数据requestAttributes
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        String cookie = request.getHeader("Cookie");
        // 2.添加cookie值
        requestTemplate.header("Cookie",cookie);
      }

    };
    return requestInterceptor;
  }

}
