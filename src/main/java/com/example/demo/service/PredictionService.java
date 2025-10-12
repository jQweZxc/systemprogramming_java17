package com.example.demo.service;

import com.example.demo.dto.RoutePredictionDTO;
import com.example.demo.model.PassengerCount;
import com.example.demo.model.Route;
import com.example.demo.repository.PassengerCountRepository;
import com.example.demo.repository.RouteRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
     * Прогноз загруженности для конкретного маршрута, времени и остановки
     */
    public RoutePredictionDTO getPredictionForRouteAndTime(String routeId, LocalDateTime time, String stopId) {
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
     * Прогноз на весь день по маршруту
     */
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
     * Расчет текущей загруженности автобуса
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
}