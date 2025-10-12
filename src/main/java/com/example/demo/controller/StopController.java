package com.example.demo.controller;

import com.example.demo.config.ResourceNotFoundException;
import com.example.demo.model.Stop;
import com.example.demo.service.StopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * КОНТРОЛЛЕР ДЛЯ РАБОТЫ С ОСТАНОВКАМИ
 */
@RestController
@RequestMapping("/api/stops")
@Tag(name = "📍 Остановки", description = "API для управления остановками общественного транспорта")
public class StopController {
    
    private final StopService stopService;

    public StopController(StopService stopService) {
        this.stopService = stopService;
    }

    /**
     * ПОЛУЧЕНИЕ ВСЕХ ОСТАНОВОК
     */
    @Operation(summary = "Получить все остановки")
    @GetMapping
    public ResponseEntity<List<Stop>> getAllStops() {
        try {
            List<Stop> stops = stopService.getAll();
            return ResponseEntity.ok(stops);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка остановок: " + e.getMessage());
        }
    }

    /**
     * ПОИСК БЛИЖАЙШИХ ОСТАНОВОК ПО КООРДИНАТАМ
     */
    @Operation(summary = "Найти ближайшие остановки")
    @GetMapping("/nearby")
    public ResponseEntity<List<Stop>> getNearbyStops(
            @Parameter(description = "Широта", example = "55.7558", required = true)
            @RequestParam Double lat,
            
            @Parameter(description = "Долгота", example = "37.6173", required = true)
            @RequestParam Double lon) {
        
        try {
            // Валидация координат
            if (lat == null || lon == null) {
                throw new IllegalArgumentException("Координаты не могут быть пустыми");
            }
            
            if (lat < -90 || lat > 90) {
                throw new IllegalArgumentException("Широта должна быть в диапазоне от -90 до 90");
            }
            
            if (lon < -180 || lon > 180) {
                throw new IllegalArgumentException("Долгота должна быть в диапазоне от -180 до 180");
            }
            
            List<Stop> stops = stopService.getNearbyStops(lat, lon);
            return ResponseEntity.ok(stops);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверные параметры координат: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске ближайших остановок: " + e.getMessage());
        }
    }

    /**
     * ДОБАВЛЕНИЕ НОВОЙ ОСТАНОВКИ
     */
    @Operation(summary = "Добавить новую остановку")
    @PostMapping
    public ResponseEntity<Stop> addStop(
            @Parameter(description = "Данные остановки", required = true)
            @RequestBody @Valid Stop stop) {
        
        try {
            // Дополнительная бизнес-валидация
            if (stop.getLat() < -90 || stop.getLat() > 90) {
                throw new IllegalArgumentException("Широта должна быть в диапазоне от -90 до 90");
            }
            
            if (stop.getLon() < -180 || stop.getLon() > 180) {
                throw new IllegalArgumentException("Долгота должна быть в диапазоне от -180 до 180");
            }
            
            Stop created = stopService.create(stop);
            return ResponseEntity.ok(created);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка валидации данных остановки: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании остановки: " + e.getMessage());
        }
    }

    /**
     * ПОЛУЧЕНИЕ ИНФОРМАЦИИ О КОНКРЕТНОЙ ОСТАНОВКЕ
     */
    @Operation(summary = "Получить информацию об остановке по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Stop> getStopById(
            @Parameter(description = "ID остановки", required = true)
            @PathVariable Long id) {
        
        Stop stop = stopService.getById(id);
        if (stop == null) {
            throw new ResourceNotFoundException("Остановка", id);
        }
        return ResponseEntity.ok(stop);
    }

    /**
     * ОБНОВЛЕНИЕ ИНФОРМАЦИИ ОБ ОСТАНОВКЕ
     */
    @Operation(summary = "Обновить информацию об остановке")
    @PutMapping("/{id}")
    public ResponseEntity<Stop> updateStop(
            @Parameter(description = "ID остановки", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "Обновленные данные", required = true)
            @RequestBody @Valid Stop updatedStop) {
        
        Stop existing = stopService.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Остановка", id);
        }
        
        try {
            // Обновляем только разрешенные поля
            existing.setName(updatedStop.getName());
            existing.setLat(updatedStop.getLat());
            existing.setLon(updatedStop.getLon());
            
            Stop saved = stopService.create(existing); // Используем create для обновления
            return ResponseEntity.ok(saved);
            
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении остановки: " + e.getMessage());
        }
    }

    /**
     * УДАЛЕНИЕ ОСТАНОВКИ
     */
    @Operation(summary = "Удалить остановку")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStop(
            @Parameter(description = "ID остановки", required = true)
            @PathVariable Long id) {
        
        Stop existing = stopService.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Остановка", id);
        }
        
        try {
            // В реальном приложении здесь должна быть проверка на связанные данные
            // Например, нельзя удалить остановку если есть записи о пассажирах
            
            // stopService.deleteById(id); // Пока не реализовано
            throw new UnsupportedOperationException("Удаление остановок временно недоступно");
            
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении остановки: " + e.getMessage());
        }
    }
}