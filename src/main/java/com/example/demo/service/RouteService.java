package com.example.demo.service;

import com.example.demo.model.Route;
import com.example.demo.repository.RouteRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * СЕРВИС ДЛЯ РАБОТЫ С МАРШРУТАМИ
 */
@Service
public class RouteService {
    
    private final RouteRepository routeRepository;
    
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }
    
    /**
     * ПОЛУЧЕНИЕ ВСЕХ МАРШРУТОВ
     * 
     * Кэшируется, так как маршруты меняются редко
     */
    @Cacheable(value = "routes", key = "'all'")
    public List<Route> getAll() {
        return routeRepository.findAll();
    }
    
    /**
     * ПОЛУЧЕНИЕ МАРШРУТА ПО ID
     */
    @Cacheable(value = "routes", key = "#id")
    public Route getById(Long id) {
        return routeRepository.findById(id).orElse(null);
    }
    
    /**
     * СОЗДАНИЕ НОВОГО МАРШРУТА
     * 
     * Очищаем кэш всех маршрутов при добавлении нового
     */
    @CacheEvict(value = "routes", key = "'all'")
    public Route create(Route route) {
        return routeRepository.save(route);
    }
}