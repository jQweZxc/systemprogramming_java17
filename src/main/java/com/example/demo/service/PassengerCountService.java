package com.example.demo.service;

import com.example.demo.dto.PassengerResponseDto;
import com.example.demo.model.PassengerCount;
import com.example.demo.repository.PassengerCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerCountService {
    
    private final PassengerCountRepository passengerCountRepository;
    
    // === DTO методы для чтения ===
    
    @Cacheable(value = "passengers", key = "'all'")
    public List<PassengerResponseDto> getAll() {
        return passengerCountRepository.findAllAsDto();
    }
    
    @Cacheable(value = "passengers", key = "#id")
    public PassengerResponseDto getById(Long id) {
        return passengerCountRepository.findDtoById(id)
            .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
    }
    
    @Cacheable(value = "passengers", key = "{'byStop', #stopId}")
    public List<PassengerResponseDto> getByStopId(Long stopId) {
        return passengerCountRepository.findByStopIdAsDto(stopId);
    }
    
    @Cacheable(value = "passengers", key = "{'byBus', #busId}")
    public List<PassengerResponseDto> getByBusId(Long busId) {
        return passengerCountRepository.findByBusIdAsDto(busId);
    }
    
    @Cacheable(value = "passengers", key = "{'byRoute', #routeId}")
    public List<PassengerResponseDto> getByRouteId(Long routeId) {
        return passengerCountRepository.findByRouteIdAsDto(routeId);
    }
    
    // === Методы для сущностей (для создания/обновления) ===
    
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
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
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
    
    // === Дополнительные методы ===
    
    @Cacheable(value = "passengers", key = "{'byPeriod', #start, #end}")
    public List<PassengerCount> getByPeriod(LocalDateTime start, LocalDateTime end) {
        return passengerCountRepository.findByTimestampBetween(start, end);
    }
    
    /**
     * Получить статистику по остановке за период
     */
    public Object[] getStatsByStopAndPeriod(Long stopId, LocalDateTime start, LocalDateTime end) {
        return passengerCountRepository.findPassengerStatsByStopAndPeriod(stopId, start, end);
    }
}