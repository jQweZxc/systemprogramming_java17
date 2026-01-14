package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        
        simpleCacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("products"), 
            new ConcurrentMapCache("product"),
            new ConcurrentMapCache("buses"),
            new ConcurrentMapCache("stops"),
            new ConcurrentMapCache("routes"),
            new ConcurrentMapCache("passengers"),
            new ConcurrentMapCache("predictions")
        ));
        
        return simpleCacheManager;
    }
}