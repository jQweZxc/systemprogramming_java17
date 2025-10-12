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

/**
 * КОНФИГУРАЦИЯ КЭШИРОВАНИЯ ДАННЫХ
 * 
 * Этот класс настраивает кэширование в Spring Boot приложении.
 * Кэширование позволяет хранить часто используемые данные в памяти
 * для ускорения доступа и снижения нагрузки на базу данных.
 */
@Configuration // Помечает класс как класс конфигурации Spring
@EnableCaching // Включает поддержку кэширования в приложении
public class CacheConfig { 
    
    /**
     * СОЗДАНИЕ МЕНЕДЖЕРА КЭША
     * 
     * Этот метод создает и настраивает менеджер кэша, который будет
     * управлять всеми кэшами в приложении.
     * 
     * @return CacheManager - менеджер кэша, управляющий кэшами приложения
     */
    @Bean // Помечает метод как создающий bean, который управляется Spring
    CacheManager cacheManager() {
        // Создаем простой менеджер кэша на основе ConcurrentMap
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        
        // Настраиваем кэши приложения:
        // - "products" - для хранения списков продуктов (множество записей)
        // - "product"  - для хранения отдельных продуктов (одна запись)
        simpleCacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("products"), // Кэш для коллекций продуктов
            new ConcurrentMapCache("product")   // Кэш для отдельных продуктов
        ));
        
        return simpleCacheManager;
    } 

}