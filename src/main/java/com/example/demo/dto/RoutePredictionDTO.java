package com.example.demo.dto;

import com.example.demo.model.Route;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;

public record RoutePredictionDTO(
    @NotNull(message = "Маршрут не может быть пустым")
    Route route,
    
    @NotNull(message = "Время отправления не может быть пустым")
    LocalDateTime departureTime,
    
    @Min(0) @Max(100)
    Integer predictedLoad // % загруженности от 0 до 100
) {}