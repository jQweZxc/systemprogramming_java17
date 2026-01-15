package com.example.demo.controller;

import com.example.demo.dto.RoutePredictionDTO;
import com.example.demo.service.PredictionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {
    
    private final PredictionService predictionService;
    
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }
    
    @PreAuthorize("hasAuthority('PREDICTION_READ')")
    @GetMapping
    public RoutePredictionDTO getPrediction(
            @RequestParam String route,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time,
            @RequestParam(required = false) String stop) {
        return predictionService.getPredictionForRouteAndTime(route, time, stop);
    }
    
    @PreAuthorize("hasAuthority('PREDICTION_READ')")
    @GetMapping("/daily/{routeId}")
    public List<RoutePredictionDTO> getDailyPredictions(@PathVariable String routeId) {
        return predictionService.getDailyPredictions(routeId);
    }
    
    @PreAuthorize("hasAuthority('PREDICTION_READ')")
    @GetMapping("/current-load/{busId}")
    public Integer getCurrentLoad(@PathVariable Long busId) {
        return predictionService.calculateCurrentLoad(busId);
    }
}