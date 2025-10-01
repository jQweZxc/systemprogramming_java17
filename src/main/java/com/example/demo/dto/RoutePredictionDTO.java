package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.Route;

public record RoutePredictionDTO(Route route, LocalDateTime departureTime, Integer predictedLoad) {
}