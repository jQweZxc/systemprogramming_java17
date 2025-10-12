package com.example.demo.controller;

import com.example.demo.config.ResourceNotFoundException;
import com.example.demo.model.Bus;
import com.example.demo.model.Route;
import com.example.demo.service.BusService;
import com.example.demo.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * КОНТРОЛЛЕР ДЛЯ РАБОТЫ С МАРШРУТАМИ И АВТОБУСАМИ
 */
@RestController
@RequestMapping("/api")
@Tag(name = "🛣️ Маршруты и автобусы", description = "API для управления маршрутами и автобусами")
public class RouteController {
    
    private final RouteService routeService;
    private final BusService busService;

    public RouteController(RouteService routeService, BusService busService) {
        this.routeService = routeService;
        this.busService = busService;
    }

    /**
     * ПОЛУЧЕНИЕ ВСЕХ МАРШРУТОВ
     */
    @Operation(summary = "Получить все маршруты")
    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        try {
            List<Route> routes = routeService.getAll();
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка маршрутов: " + e.getMessage());
        }
    }

    /**
     * ПОЛУЧЕНИЕ МАРШРУТА ПО ID
     */
    @Operation(summary = "Получить маршрут по ID")
    @GetMapping("/routes/{id}")
    public ResponseEntity<Route> getRouteById(
            @Parameter(description = "ID маршрута", required = true)
            @PathVariable Long id) {
        
        Route route = routeService.getById(id);
        if (route == null) {
            throw new ResourceNotFoundException("Маршрут", id);
        }
        return ResponseEntity.ok(route);
    }

    /**
     * ДОБАВЛЕНИЕ НОВОГО МАРШРУТА
     */
    @Operation(summary = "Добавить новый маршрут")
    @PostMapping("/routes")
    public ResponseEntity<Route> addRoute(
            @Parameter(description = "Данные маршрута", required = true)
            @RequestBody @Valid Route route) {
        
        try {
            Route created = routeService.create(route);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании маршрута: " + e.getMessage());
        }
    }

    /**
     * ПОЛУЧЕНИЕ АВТОБУСОВ ПО МАРШРУТУ
     */
    @Operation(summary = "Получить автобусы по маршруту")
    @GetMapping("/routes/{routeId}/buses")
    public ResponseEntity<List<Bus>> getBusesByRoute(
            @Parameter(description = "ID маршрута", required = true)
            @PathVariable Long routeId) {
        
        try {
            List<Bus> buses = busService.getByRouteId(routeId);
            
            if (buses.isEmpty()) {
                throw new ResourceNotFoundException("Автобусы для маршрута", routeId);
            }
            
            return ResponseEntity.ok(buses);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении автобусов: " + e.getMessage());
        }
    }

    /**
     * ДОБАВЛЕНИЕ НОВОГО АВТОБУСА
     */
    @Operation(summary = "Добавить новый автобус")
    @PostMapping("/buses")
    public ResponseEntity<Bus> addBus(
            @Parameter(description = "Данные автобуса", required = true)
            @RequestBody @Valid Bus bus) {
        
        try {
            // Валидация бизнес-логики
            if (bus.getModel() == null || bus.getModel().trim().isEmpty()) {
                throw new IllegalArgumentException("Модель автобуса не может быть пустой");
            }
            
            Bus created = busService.create(bus);
            return ResponseEntity.ok(created);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка валидации данных автобуса: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании автобуса: " + e.getMessage());
        }
    }

    /**
     * ПОЛУЧЕНИЕ АВТОБУСА ПО ID
     */
    @Operation(summary = "Получить автобус по ID")
    @GetMapping("/buses/{id}")
    public ResponseEntity<Bus> getBusById(
            @Parameter(description = "ID автобуса", required = true)
            @PathVariable Long id) {
        
        Bus bus = busService.getById(id);
        if (bus == null) {
            throw new ResourceNotFoundException("Автобус", id);
        }
        return ResponseEntity.ok(bus);
    }
}