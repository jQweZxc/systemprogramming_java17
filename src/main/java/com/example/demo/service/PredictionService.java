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
    
    public PredictionService(PassengerCountRepository passengerCountRepository, 
                           RouteRepository routeRepository) {
        this.passengerCountRepository = passengerCountRepository;
        this.routeRepository = routeRepository;
    }
    
    /**
     * ПРОГНОЗ ЗАГРУЖЕННОСТИ ДЛЯ КОНКРЕТНОГО МАРШРУТА И ВРЕМЕНИ
     * 
     * Результаты кэшируются на 10 минут для одинаковых параметров
     * Ключ кэша включает routeId, hour и stopId для уникальности
     * 
     * @Cacheable - результат метода кэшируется, при повторных вызовах с теми же параметрами
     *              данные берутся из кэша вместо выполнения метода
     */
    @Cacheable(value = "predictions", key = "{#routeId, #time.hour, #stopId}")
    public RoutePredictionDTO getPredictionForRouteAndTime(String routeId, LocalDateTime time, String stopId) {
        // Имитация сложных вычислений для демонстрации benefits кэширования
        simulateHeavyCalculation();
        
        Route route = routeRepository.findById(Long.parseLong(routeId)).orElse(null);
        if (route == null) {
            return null;
        }
        
        // Расчет средней загруженности в это время за последние 30 дней
        LocalDateTime startDate = time.minusDays(30);
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
        
        // Нормализация до процентов (предполагаем макс. вместимость 50 человек)
        int predictedLoad = (int) Math.min(100, Math.max(0, (averageLoad / 50.0) * 100));
        
        return new RoutePredictionDTO(route, time, predictedLoad);
    }
    
    /**
     * ПРОГНОЗ НА ВЕСЬ ДЕНЬ ПО МАРШРУТУ
     * 
     * Кэшируется на 1 час, так как данные меняются реже
     * Ключ включает только routeId и текущую дату
     */
    @Cacheable(value = "daily_predictions", key = "{#routeId, T(java.time.LocalDate).now()}")
    public List<RoutePredictionDTO> getDailyPredictions(String routeId) {
        List<RoutePredictionDTO> predictions = new ArrayList<>();
        Route route = routeRepository.findById(Long.parseLong(routeId)).orElse(null);
        
        if (route != null) {
            LocalDateTime today = LocalDateTime.now().with(LocalTime.MIN);
            
            // Прогноз на каждый час с 6:00 до 22:00
            for (int hour = 6; hour <= 22; hour++) {
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
     * 
     * Не кэшируется, так как данные должны быть актуальными
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
            
        return Math.max(0, currentLoad); // Не может быть отрицательным
    }
    
    /**
     * ОЧИСТКА КЭША ПРОГНОЗОВ ПО РАСПИСАНИЮ
     * 
     * Выполняется каждые 10 минут для обеспечения актуальности данных
     * @CacheEvict - очищает указанный кэш полностью
     */
    @Scheduled(fixedRate = 600000) // 10 минут
    @CacheEvict(value = {"predictions", "daily_predictions"}, allEntries = true)
    public void evictPredictionCaches() {
        // Метод не требует реализации, Spring автоматически очистит кэш
        System.out.println("Кэш прогнозов очищен: " + LocalDateTime.now());
    }
    
    /**
     * ИМИТАЦИЯ СЛОЖНЫХ ВЫЧИСЛЕНИЙ
     * 
     * Для демонстрации benefits кэширования
     */
    private void simulateHeavyCalculation() {
        try {
            // Имитация сложных вычислений (500ms)
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}