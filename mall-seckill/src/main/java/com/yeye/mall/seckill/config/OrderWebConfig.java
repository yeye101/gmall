package com.yeye.mall.seckill.config;

import com.yeye.mall.seckill.interceptor.LoginUserInterceptor;
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
