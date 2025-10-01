package com.example.demo.config;

import java.beans.BeanProperty;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableCaching
public class CacheConfig { 
    @Bean
    CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager=new SimpleCacheManager();
        simpleCacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("products"),new ConcurrentMapCache("product")));
        return simpleCacheManager;
    } 

}
