package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * КОНФИГУРАЦИЯ КЭШИРОВАНИЯ ДАННЫХ ДЛЯ СИСТЕМЫ ПАССАЖИРОПОТОКА
 * 
 * Настраивает кэширование для ускорения доступа к часто запрашиваемым данным
 * и снижения нагрузки на базу данных.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * СОЗДАНИЕ МЕНЕДЖЕРА КЭША ДЛЯ СИСТЕМЫ ПАССАЖИРОПОТОКА
     * 
     * Настраивает различные кэши для разных типов данных:
     * - predictions: кэш прогнозов загруженности
     * - passenger_counts: кэш данных о пассажирах  
     * - stops: кэш информации об остановках
     * - routes: кэш данных о маршрутах
     * 
     * @return CacheManager - менеджер кэша для управления всеми кэшами приложения
     */
    @Bean
    CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        
        // Настраиваем кэши для системы пассажиропотока
        simpleCacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("predictions"),      // Кэш прогнозов загруженности
            new ConcurrentMapCache("passenger_counts"), // Кэш данных о пассажирах
            new ConcurrentMapCache("stops"),           // Кэш информации об остановках
            new ConcurrentMapCache("routes"),          // Кэш данных о маршрутах
            new ConcurrentMapCache("buses"),           // Кэш информации об автобусах
            new ConcurrentMapCache("daily_predictions") // Кэш ежедневных прогнозов
        ));
        
        return simpleCacheManager;
    }
}