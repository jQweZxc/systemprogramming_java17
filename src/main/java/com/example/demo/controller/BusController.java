package com.example.demo.controller;

import com.example.demo.model.Bus;
import com.example.demo.service.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {
    
    private final BusService busService;
    
    public BusController(BusService busService) {
        this.busService = busService;
    }
    
    @GetMapping
    public List<Bus> getAllBuses() {
        return busService.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        Bus bus = busService.getById(id);
        return bus != null ? ResponseEntity.ok(bus) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Bus> createBus(@RequestBody Bus bus) {
        Bus createdBus = busService.create(bus);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBus);
    }
    
    @GetMapping("/route/{routeId}")
    public List<Bus> getBusesByRoute(@PathVariable Long routeId) {
        return busService.getByRouteId(routeId);
    }
}