package com.yeye.mall.member.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("com.yeye.mall.member.dao")
public class MybatisConfig {

  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
    // 溢出总页数后是否进行处理(默认不处理)
    paginationInnerInterceptor.setOverflow(true);
    // 单页分页条数限制(默认无限制)
    paginationInnerInterceptor.setMaxLimit(1000L);
    interceptor.addInnerInterceptor(paginationInnerInterceptor);
    return interceptor;
  }


}
