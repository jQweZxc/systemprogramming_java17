package com.example.demo.service;

import com.example.demo.model.Bus;
import com.example.demo.repository.BusRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class BusService {
    
    private final BusRepository busRepository;
    
    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }
    

    @Cacheable(value = "buses", key = "'all'")
    public List<Bus> getAll() {
        return busRepository.findAll();
    }
    
    @Cacheable(value = "buses", key = "#id")
    public Bus getById(Long id) {
        return busRepository.findById(id).orElse(null);
    }
    
    @CacheEvict(value = "buses", key = "'all'")
    @Transactional
    public Bus create(Bus bus) {
        return busRepository.save(bus);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "buses", key = "#id"),
        @CacheEvict(value = "buses", key = "'all'"),
        @CacheEvict(value = "buses", key = "{'byRoute', #bus.route?.id}")
    })
    @Transactional
    public Bus update(Long id, Bus bus) {
        return busRepository.findById(id)
                .map(existingBus -> {
                    existingBus.setModel(bus.getModel());
                    existingBus.setRoute(bus.getRoute());
                    return busRepository.save(existingBus);
                })
                .orElse(null);
    }

    @Caching(evict = {
        @CacheEvict(value = "buses", key = "#id"),
        @CacheEvict(value = "buses", key = "'all'")
    })
    @Transactional
    public boolean delete(Long id) {
        if (busRepository.existsById(id)) {
            busRepository.deleteById(id);
            return true;
        }
        return false;
    }
    

    @Cacheable(value = "buses", key = "{'byRoute', #routeId}")
    public List<Bus> getByRouteId(Long routeId) {
        return busRepository.findByRouteId(routeId);
    }
    

    @Cacheable(value = "buses", key = "{'byModel', #model}")
    public List<Bus> getByModel(String model) {
        return busRepository.findByModelContainingIgnoreCase(model);
    }

    public Optional<Bus> getBusById(Long id) {
        return Optional.ofNullable(getById(id));
    }
    
}