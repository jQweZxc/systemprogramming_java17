package com.example.demo.service;

import com.example.demo.model.Stop;
import com.example.demo.repository.StopRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class StopService {
    
    private final StopRepository stopRepository;
    
    public StopService(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }
    

    @Cacheable(value = "stops", key = "'all'")
    public List<Stop> getAll() {
        return stopRepository.findAll();
    }

    @Cacheable(value = "stops", key = "#id")
    public Stop getById(Long id) {
        return stopRepository.findById(id).orElse(null);
    }
    

    @CacheEvict(value = "stops", key = "'all'")
    @Transactional
    public Stop create(Stop stop) {
        return stopRepository.save(stop);
    }
    

    @Caching(evict = {
        @CacheEvict(value = "stops", key = "#id"),
        @CacheEvict(value = "stops", key = "'all'")
    })
    @Transactional
    public Stop update(Long id, Stop stop) {
        return stopRepository.findById(id)
                .map(existingStop -> {
                    existingStop.setName(stop.getName());
                    existingStop.setLat(stop.getLat());
                    existingStop.setLon(stop.getLon());
                    return stopRepository.save(existingStop);
                })
                .orElse(null);
    }

    @Caching(evict = {
        @CacheEvict(value = "stops", key = "#id"),
        @CacheEvict(value = "stops", key = "'all'")
    })
    @Transactional
    public boolean delete(Long id) {
        if (stopRepository.existsById(id)) {
            stopRepository.deleteById(id);
            return true;
        }
        return false;
    }
    

    @Cacheable(value = "stops", key = "{'nearby', #lat, #lon}")
    public List<Stop> getNearbyStops(Double lat, Double lon) {
        return stopRepository.findNearbyStops(lat, lon);
    }

    @Cacheable(value = "stops", key = "{'byName', #name}")
    public List<Stop> getByName(String name) {
        return stopRepository.findByNameContainingIgnoreCase(name);
    }
}