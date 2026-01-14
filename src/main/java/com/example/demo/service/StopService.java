package com.example.demo.service;

import com.example.demo.model.Stop;
import com.example.demo.repository.StopRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * СЕРВИС ДЛЯ РАБОТЫ С ОСТАНОВКАМИ
 * 
 * Управляет данными об остановках с использованием кэширования
 * для часто запрашиваемых данных
 */
@Service
public class StopService {
    
    private final StopRepository stopRepository;
    
    public StopService(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }
    
    /**
     * ПОЛУЧЕНИЕ ВСЕХ ОСТАНОВОК
     * 
     * Кэшируется на длительное время, так как остановки редко меняются
     */
    @Cacheable(value = "stops", key = "'all'")
    public List<Stop> getAll() {
        return stopRepository.findAll();
    }
    
    /**
     * ПОЛУЧЕНИЕ ОСТАНОВКИ ПО ID
     * 
     * Каждая остановка кэшируется отдельно по её ID
     */
    @Cacheable(value = "stops", key = "#id")
    public Stop getById(Long id) {
        return stopRepository.findById(id).orElse(null);
    }
    
    /**
     * СОЗДАНИЕ НОВОЙ ОСТАНОВКИ
     * 
     * При создании новой остановки очищаем кэш всех остановок,
     * так как список изменился
     */
    @CacheEvict(value = "stops", key = "'all'")
    @Transactional
    public Stop create(Stop stop) {
        return stopRepository.save(stop);
    }
    
    /**
     * ОБНОВЛЕНИЕ ОСТАНОВКИ
     */
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
    
    /**
     * УДАЛЕНИЕ ОСТАНОВКИ
     */
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
    
    /**
     * ПОИСК БЛИЖАЙШИХ ОСТАНОВОК ПО КООРДИНАТАМ
     * 
     * Кэшируется по координатам, так как запросы с одинаковыми координатами
     * будут возвращать одинаковые результаты
     */
    @Cacheable(value = "stops", key = "{'nearby', #lat, #lon}")
    public List<Stop> getNearbyStops(Double lat, Double lon) {
        return stopRepository.findNearbyStops(lat, lon);
    }
    
    /**
     * ПОИСК ОСТАНОВОК ПО НАЗВАНИЮ
     */
    @Cacheable(value = "stops", key = "{'byName', #name}")
    public List<Stop> getByName(String name) {
        return stopRepository.findByNameContainingIgnoreCase(name);
    }
}