package com.yeye.mall.member.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  RedisCacheConfiguration redisCacheConfiguration() {

    /**
     * entryTtl:过期时间
     * serializeKeysWith：Keys序列化
     * serializeValuesWith：Values序列化
     */
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
      .entryTtl(Duration.ofHours(1))
      .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
      .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));
    return config;
  }

}
