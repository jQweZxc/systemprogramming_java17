package com.example.demo.service;

import com.example.demo.model.Bus;
import com.example.demo.repository.BusRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * СЕРВИС ДЛЯ РАБОТЫ С АВТОБУСАМИ
 */
@Service
public class BusService {
    
    private final BusRepository busRepository;
    
    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }
    
    /**
     * ПОЛУЧЕНИЕ ВСЕХ АВТОБУСОВ
     */
    @Cacheable(value = "buses", key = "'all'")
    public List<Bus> getAll() {
        return busRepository.findAll();
    }
    
    /**
     * ПОЛУЧЕНИЕ АВТОБУСА ПО ID
     */
    @Cacheable(value = "buses", key = "#id")
    public Bus getById(Long id) {
        return busRepository.findById(id).orElse(null);
    }
    
    /**
     * СОЗДАНИЕ НОВОГО АВТОБУСА
     */
    @CacheEvict(value = "buses", key = "'all'")
    public Bus create(Bus bus) {
        return busRepository.save(bus);
    }
    
    /**
     * ПОЛУЧЕНИЕ АВТОБУСОВ ПО МАРШРУТУ
     * 
     * Кэшируется по ID маршрута
     */
    @Cacheable(value = "buses", key = "{'byRoute', #routeId}")
    public List<Bus> getByRouteId(Long routeId) {
        return busRepository.findByRouteId(routeId);
    }
}