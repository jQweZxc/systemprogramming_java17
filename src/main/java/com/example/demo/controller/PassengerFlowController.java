package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.dto.PassengerCountDTO;
import com.example.demo.dto.RoutePredictionDTO;
import com.example.demo.model.PassengerCount;
import com.example.demo.service.PassengerCountService;
import com.example.demo.service.PredictionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * КОНТРОЛЛЕР ДЛЯ РАБОТЫ С ПАССАЖИРОПОТОКОМ И ПРОГНОЗАМИ
 */
@RestController
@RequestMapping("/api")
@Tag(name = "📊 Пассажиропоток и прогнозы", description = "API для учета пассажиров и прогнозирования загруженности")
public class PassengerFlowController {
    
    private final PassengerCountService passengerCountService;
    private final PredictionService predictionService;

    public PassengerFlowController(PassengerCountService passengerCountService, 
                                PredictionService predictionService) {
        this.passengerCountService = passengerCountService;
        this.predictionService = predictionService;
    }

/**
 * ПОЛУЧЕНИЕ ВСЕХ ЗАПИСЕЙ О ПАССАЖИРАХ
 */
@Operation(summary = "Получить все записи о пассажиропотоке")
@GetMapping("/passengers")
public ResponseEntity<List<PassengerCount>> getAllPassengerCounts() {
    try {
        List<PassengerCount> passengers = passengerCountService.getAll();
        return ResponseEntity.ok(passengers);
    } catch (Exception e) {
        throw new RuntimeException("Ошибка при получении данных о пассажирах: " + e.getMessage());
    }
}


    /**
     * ПОЛУЧЕНИЕ ЗАПИСИ О ПАССАЖИРАХ ПО ID
     */
    @Operation(summary = "Получить запись о пассажирах по ID")
    @GetMapping("/passengers/{id}")
    public ResponseEntity<PassengerCount> getPassengerCountById(
            @Parameter(description = "ID записи о пассажирах", required = true)
            @PathVariable Long id) {
        
        PassengerCount passengerCount = passengerCountService.getById(id);
        if (passengerCount == null) {
            throw new ResourceNotFoundException("Запись о пассажирах", id);
        }
        return ResponseEntity.ok(passengerCount);
    }

    /**
     * ДОБАВЛЕНИЕ ДАННЫХ С ДАТЧИКА УЧЕТА ПАССАЖИРОВ
     */
    @Operation(summary = "Добавить данные с датчика пассажиропотока")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Данные успешно сохранены"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PostMapping("/passengers")
    public ResponseEntity<PassengerCount> addPassengerCount(
            @Parameter(description = "Данные о пассажирах", required = true)
            @RequestBody @Valid PassengerCount passengerCount) {
        
        try {
            // Валидация бизнес-логики: сумма вошедших и вышедших должна быть логичной
            if (passengerCount.getEntered() < 0 || passengerCount.getExited() < 0) {
                throw new IllegalArgumentException("Количество пассажиров не может быть отрицательным");
            }
            
            if (passengerCount.getEntered() > 100 || passengerCount.getExited() > 100) {
                throw new IllegalArgumentException("Слишком большое количество пассажиров за одну запись");
            }
            
            PassengerCount created = passengerCountService.create(passengerCount);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка валидации данных: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении данных о пассажирах: " + e.getMessage());
        }
    }

    /**
     * ОБНОВЛЕНИЕ ДАННЫХ О ПАССАЖИРАХ
     */
    @Operation(summary = "Обновить данные о пассажирах")
    @PutMapping("/passengers/{id}")
    public ResponseEntity<PassengerCount> updatePassengerCount(
            @Parameter(description = "ID записи", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "Обновленные данные", required = true)
            @RequestBody @Valid PassengerCount updatedPassengerCount) {
        
        PassengerCount existing = passengerCountService.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Запись о пассажирах", id);
        }
        
        try {
            PassengerCount updated = passengerCountService.updateById(id, updatedPassengerCount);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении данных: " + e.getMessage());
        }
    }

    /**
     * УДАЛЕНИЕ ДАННЫХ О ПАССАЖИРАХ
     */
    @Operation(summary = "Удалить запись о пассажирах")
    @DeleteMapping("/passengers/{id}")
    public ResponseEntity<Void> deletePassengerCount(
            @Parameter(description = "ID записи", required = true)
            @PathVariable Long id) {
        
        PassengerCount existing = passengerCountService.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Запись о пассажирах", id);
        }
        
        try {
            passengerCountService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении данных: " + e.getMessage());
        }
    }

    /**
     * ПРОГНОЗ ЗАГРУЖЕННОСТИ ДЛЯ КОНКРЕТНОГО МАРШРУТА И ВРЕМЕНИ
     */
    @Operation(summary = "Прогноз загруженности маршрута")
    @GetMapping("/predictors")
    public ResponseEntity<RoutePredictionDTO> getPrediction(
            @Parameter(description = "Идентификатор маршрута", example = "7A", required = true)
            @RequestParam String route,
            
            @Parameter(description = "Время отправления", example = "2024-12-19T15:00:00", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time,
            
            @Parameter(description = "Идентификатор остановки (опционально)", example = "K")
            @RequestParam(required = false) String stop) {
        
        try {
            // Валидация входных параметров
            if (route == null || route.trim().isEmpty()) {
                throw new IllegalArgumentException("Идентификатор маршрута не может быть пустым");
            }
            
            if (time == null) {
                throw new IllegalArgumentException("Время не может быть пустым");
            }
            
            if (time.isBefore(LocalDateTime.now().minusDays(1))) {
                throw new IllegalArgumentException("Время не может быть в прошлом");
            }
            
            RoutePredictionDTO prediction = predictionService.getPredictionForRouteAndTime(route, time, stop);
            
            if (prediction == null) {
                throw new ResourceNotFoundException("Маршрут", route);
            }
            
            return ResponseEntity.ok(prediction);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверные параметры запроса: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расчете прогноза: " + e.getMessage());
        }
    }

    /**
     * ПРОГНОЗ ЗАГРУЖЕННОСТИ НА ВЕСЬ ДЕНЬ ПО МАРШРУТУ
     */
    @Operation(summary = "Прогноз загруженности на весь день")
    @GetMapping("/predictors/daily")
    public ResponseEntity<List<RoutePredictionDTO>> getDailyPredictions(
            @Parameter(description = "Идентификатор маршрута", example = "7A", required = true)
            @RequestParam String route) {
        
        try {
            if (route == null || route.trim().isEmpty()) {
                throw new IllegalArgumentException("Идентификатор маршрута не может быть пустым");
            }
            
            List<RoutePredictionDTO> predictions = predictionService.getDailyPredictions(route);
            
            if (predictions.isEmpty()) {
                throw new ResourceNotFoundException("Маршрут", route);
            }
            
            return ResponseEntity.ok(predictions);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверные параметры запроса: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расчете ежедневных прогнозов: " + e.getMessage());
        }
    }

    /**
     * ПОЛУЧЕНИЕ СТАТИСТИКИ ПО ОСТАНОВКЕ
     */
    @Operation(summary = "Статистика пассажиропотока по остановке")
    @GetMapping("/stops/{id}/stats")
    public ResponseEntity<?> getStopStatistics(
            @Parameter(description = "ID остановки", example = "1", required = true)
            @PathVariable Long id) {
        
        try {
            List<PassengerCount> stopData = passengerCountService.getByStopId(id);
            
            if (stopData.isEmpty()) {
                throw new ResourceNotFoundException("Остановка", id);
            }
            
            int totalEntered = stopData.stream().mapToInt(PassengerCount::getEntered).sum();
            int totalExited = stopData.stream().mapToInt(PassengerCount::getExited).sum();
            
            // Создаем объект статистики
            StopStatistics stats = new StopStatistics(id, totalEntered, totalExited, totalEntered - totalExited);
            
            return ResponseEntity.ok(stats);
            
        } catch (ResourceNotFoundException e) {
            throw e; // Пробрасываем дальше для обработки в GlobalExceptionHandler
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расчете статистики: " + e.getMessage());
        }
    }

    /**
     * DTO ДЛЯ СТАТИСТИКИ ОСТАНОВКИ
     */
    public static class StopStatistics {
        private final Long stopId;
        private final int totalEntered;
        private final int totalExited;
        private final int netPassengers;

        public StopStatistics(Long stopId, int totalEntered, int totalExited, int netPassengers) {
            this.stopId = stopId;
            this.totalEntered = totalEntered;
            this.totalExited = totalExited;
            this.netPassengers = netPassengers;
        }

        // Геттеры
        public Long getStopId() { return stopId; }
        public int getTotalEntered() { return totalEntered; }
        public int getTotalExited() { return totalExited; }
        public int getNetPassengers() { return netPassengers; }
    }
}