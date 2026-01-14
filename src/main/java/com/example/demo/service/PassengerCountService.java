package com.example.demo.service;

import com.example.demo.model.PassengerCount;
import com.example.demo.repository.PassengerCountRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassengerCountService {
    
    private final PassengerCountRepository passengerCountRepository;

    public PassengerCountService(PassengerCountRepository passengerCountRepository) {
        this.passengerCountRepository = passengerCountRepository;
    }
    
    @Cacheable(value = "passengers", key = "'all'")
    public List<PassengerCount> getAll() {
        return passengerCountRepository.findAll();
    }
    
    @Cacheable(value = "passengers", key = "#id")
    public PassengerCount getById(Long id) {
        return passengerCountRepository.findById(id).orElse(null);
    }
    
    @CacheEvict(value = "passengers", key = "'all'")
    @Transactional
    public PassengerCount create(PassengerCount passengerCount) {
        return passengerCountRepository.save(passengerCount);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "passengers", key = "#id"),
        @CacheEvict(value = "passengers", key = "'all'")
    })
    @Transactional
    public PassengerCount update(Long id, PassengerCount updatedPassengerCount) {
        return passengerCountRepository.findById(id)
                .map(passengerCount -> {
                    passengerCount.setBus(updatedPassengerCount.getBus());
                    passengerCount.setStop(updatedPassengerCount.getStop());
                    passengerCount.setEntered(updatedPassengerCount.getEntered());
                    passengerCount.setExited(updatedPassengerCount.getExited());
                    passengerCount.setTimestamp(updatedPassengerCount.getTimestamp());
                    return passengerCountRepository.save(passengerCount);
                })
                .orElse(null);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "passengers", key = "#id"),
        @CacheEvict(value = "passengers", key = "'all'")
    })
    @Transactional
    public boolean delete(Long id) {
        if (passengerCountRepository.existsById(id)) {
            passengerCountRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Cacheable(value = "passengers", key = "{'byStop', #stopId}")
    public List<PassengerCount> getByStopId(Long stopId) {
        return passengerCountRepository.findByStopId(stopId);
    }
    
    @Cacheable(value = "passengers", key = "{'byBus', #busId}")
    public List<PassengerCount> getByBusId(Long busId) {
        return passengerCountRepository.findByBusId(busId);
    }
    
    @Cacheable(value = "passengers", key = "{'byRoute', #routeId}")
    public List<PassengerCount> getByRouteId(Long routeId) {
        return passengerCountRepository.findByRouteId(routeId);
    }
    
    @Cacheable(value = "passengers", key = "{'byPeriod', #start, #end}")
    public List<PassengerCount> getByPeriod(LocalDateTime start, LocalDateTime end) {
        return passengerCountRepository.findByTimestampBetween(start, end);
    }
    
    /**
     * АЛЬТЕРНАТИВНЫЙ МЕТОД updateById для совместимости
     */
    public PassengerCount updateById(Long id, PassengerCount updatedPassengerCount) {
        return update(id, updatedPassengerCount);
    }
    
    /**
     * АЛЬТЕРНАТИВНЫЙ МЕТОД deleteById для совместимости
     */
    public boolean deleteById(Long id) {
        return delete(id);
    }
}