package com.yeye.mall.order.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class MySeataConfig {

  @Autowired
  private DataSourceProperties dataSourceProperties;

  /**
   *  Seata控制分布式事务
   *  *  1）、每一个微服务必须创建undo_Log
   *  *  2）、安装事务协调器：seate-server
   *  *  3）、整合
   *  *      1、导入依赖
   *  *      2、解压并启动seata-server：
   *  *          registry.conf:注册中心配置    修改 registry ： nacos
   *  *      3、所有想要用到分布式事务的微服务使用seata DataSourceProxy 代理自己的数据源
   *  *      4、每个微服务，都必须导入   registry.conf   file.conf
   *  *          vgroup_mapping.{application.name}-fescar-server-group = "default"
   *  *      5、启动测试分布式事务
   *  *      6、给分布式大事务的入口标注@GlobalTransactional
   *  *      7、每一个远程的小事务用@Trabsactional
   * @param dataSourceProperties
   * @return
   */
  @Bean
  public DataSource dataSource(DataSourceProperties dataSourceProperties) {
    // properties.initializeDataSourceBuilder().type(type).build();
    HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    if (StringUtils.hasText(dataSourceProperties.getName())) {
      dataSource.setPoolName(dataSourceProperties.getName());
    }
    return new DataSourceProxy(dataSource);
  }

}
