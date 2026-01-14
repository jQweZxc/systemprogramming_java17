package com.example.demo.service;

import com.example.demo.model.Route;
import com.example.demo.repository.RouteRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RouteService {
    
    private final RouteRepository routeRepository;
    
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }
    
    @Cacheable(value = "routes", key = "'all'")
    public List<Route> getAll() {
        return routeRepository.findAll();
    }
    
    @Cacheable(value = "routes", key = "#id")
    public Route getById(Long id) {
        return routeRepository.findById(id).orElse(null);
    }
    
    @CacheEvict(value = "routes", key = "'all'")
    @Transactional
    public Route create(Route route) {
        return routeRepository.save(route);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "routes", key = "#id"),
        @CacheEvict(value = "routes", key = "'all'")
    })
    @Transactional
    public Route update(Long id, Route route) {
        return routeRepository.findById(id)
                .map(existingRoute -> {
                    existingRoute.setStops(route.getStops());
                    existingRoute.setBuses(route.getBuses());
                    return routeRepository.save(existingRoute);
                })
                .orElse(null);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "routes", key = "#id"),
        @CacheEvict(value = "routes", key = "'all'")
    })
    @Transactional
    public boolean delete(Long id) {
        if (routeRepository.existsById(id)) {
            routeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}