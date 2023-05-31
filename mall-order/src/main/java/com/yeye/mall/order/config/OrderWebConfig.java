package com.yeye.mall.order.config;

import com.yeye.mall.order.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OrderWebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor(new LoginUserInterceptor()).addPathPatterns("/**");
  }
}
