package com.example.demo.controller;

import com.example.demo.model.Route;
import com.example.demo.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    
    private final RouteService routeService;
    
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }
    
    @PreAuthorize("hasAuthority('ROUTE_READ')")
    @GetMapping
    public List<Route> getAllRoutes() {
        return routeService.getAll();
    }
    
    @PreAuthorize("hasAuthority('ROUTE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeService.getById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }
    
    @PreAuthorize("hasAuthority('ROUTE_CREATE')")
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route createdRoute = routeService.create(route);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute);
    }
    
    @PreAuthorize("hasAuthority('ROUTE_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route route) {
        Route updatedRoute = routeService.update(id, route);
        return updatedRoute != null ? ResponseEntity.ok(updatedRoute) : ResponseEntity.notFound().build();
    }
    
    @PreAuthorize("hasAuthority('ROUTE_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        boolean deleted = routeService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}