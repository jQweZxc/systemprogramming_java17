package com.example.demo.controller;

import com.example.demo.model.Route;
import com.example.demo.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    
    private final RouteService routeService;
    
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }
    
    @GetMapping
    public List<Route> getAllRoutes() {
        return routeService.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeService.getById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route createdRoute = routeService.create(route);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute);
    }
}