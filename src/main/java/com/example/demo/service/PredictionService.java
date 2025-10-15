package com.example.demo.service;

import com.example.demo.dto.RoutePredictionDTO;
import com.example.demo.model.PassengerCount;
import com.example.demo.model.Route;
import com.example.demo.repository.PassengerCountRepository;
import com.example.demo.repository.RouteRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * СЕРВИС ПРОГНОЗИРОВАНИЯ ЗАГРУЖЕННОСТИ МАРШРУТОВ
 * 
 * Содержит бизнес-логику для расчета прогнозов пассажиропотока
 * Использует кэширование для ускорения повторных запросов
 */
@Service
public class PredictionService {
    
    private final PassengerCountRepository passengerCountRepository;
    private final RouteRepository routeRepository;
    
    // Константы вынесены в поля класса
    private static final int MAX_BUS_CAPACITY = 50;
    private static final int PREDICTION_HISTORY_DAYS = 30;
    private static final int CACHE_EVICTION_RATE_MS = 600000; // 10 минут
    private static final int WORKING_DAY_START_HOUR = 6;
    private static final int WORKING_DAY_END_HOUR = 22;
    
    public PredictionService(PassengerCountRepository passengerCountRepository, 
                           RouteRepository routeRepository) {
        this.passengerCountRepository = passengerCountRepository;
        this.routeRepository = routeRepository;
    }
    
    /**
     * ПРОГНОЗ ЗАГРУЖЕННОСТИ ДЛЯ КОНКРЕТНОГО МАРШРУТА И ВРЕМЕНИ
     */
    @Cacheable(value = "predictions", key = "{#routeId, #time.hour, #stopId}")
    public RoutePredictionDTO getPredictionForRouteAndTime(String routeId, LocalDateTime time, String stopId) {
        Route route = routeRepository.findById(Long.parseLong(routeId)).orElse(null);
        if (route == null) {
            return null;
        }
        
        // Используем константу вместо "магического числа"
        LocalDateTime startDate = time.minusDays(PREDICTION_HISTORY_DAYS);
        LocalDateTime endDate = time;
        
        List<PassengerCount> historicalData = passengerCountRepository.findByTimestampBetween(startDate, endDate);
        
        // Фильтруем данные по маршруту и времени
        List<PassengerCount> filteredData = historicalData.stream()
            .filter(pc -> pc.getBus().getRoute().getId().equals(route.getId()))
            .filter(pc -> pc.getTimestamp().toLocalTime().getHour() == time.getHour())
            .toList();
        
        // Расчет средней загруженности
        double averageLoad = filteredData.stream()
            .mapToInt(pc -> pc.getEntered() - pc.getExited())
            .average()
            .orElse(0.0);
        
        // Используем константу MAX_BUS_CAPACITY вместо "50"
        int predictedLoad = (int) Math.min(100, Math.max(0, (averageLoad / MAX_BUS_CAPACITY) * 100));
        
        return new RoutePredictionDTO(route, time, predictedLoad);
    }
    
    /**
     * ПРОГНОЗ НА ВЕСЬ ДЕНЬ ПО МАРШРУТУ
     */
    @Cacheable(value = "daily_predictions", key = "{#routeId, T(java.time.LocalDate).now()}")
    public List<RoutePredictionDTO> getDailyPredictions(String routeId) {
        List<RoutePredictionDTO> predictions = new ArrayList<>();
        Route route = routeRepository.findById(Long.parseLong(routeId)).orElse(null);
        
        if (route != null) {
            LocalDateTime today = LocalDateTime.now().with(LocalTime.MIN);
            
            // Используем константы для времени работы
            for (int hour = WORKING_DAY_START_HOUR; hour <= WORKING_DAY_END_HOUR; hour++) {
                LocalDateTime predictionTime = today.withHour(hour).withMinute(0);
                RoutePredictionDTO prediction = getPredictionForRouteAndTime(routeId, predictionTime, null);
                if (prediction != null) {
                    predictions.add(prediction);
                }
            }
        }
        
        return predictions;
    }
    
    /**
     * РАСЧЕТ ТЕКУЩЕЙ ЗАГРУЖЕННОСТИ АВТОБУСА
     */
    public int calculateCurrentLoad(Long busId) {
        List<PassengerCount> todayData = passengerCountRepository.findByTimestampBetween(
            LocalDateTime.now().with(LocalTime.MIN), 
            LocalDateTime.now()
        );
        
        int currentLoad = todayData.stream()
            .filter(pc -> pc.getBus().getId().equals(busId))
            .mapToInt(pc -> pc.getEntered() - pc.getExited())
            .sum();
            
        return Math.max(0, currentLoad);
    }
    
    /**
     * ОЧИСТКА КЭША ПРОГНОЗОВ ПО РАСПИСАНИЮ
     */
    @Scheduled(fixedRate = CACHE_EVICTION_RATE_MS)
    @CacheEvict(value = {"predictions", "daily_predictions"}, allEntries = true)
    public void evictPredictionCaches() {
        // Автоматическая очистка кэша, логирование не требуется
    }
}