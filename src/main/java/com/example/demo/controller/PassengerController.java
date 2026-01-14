package com.example.demo.controller;

import com.example.demo.model.PassengerCount;
import com.example.demo.service.PassengerCountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    
    private final PassengerCountService passengerCountService;
    
    public PassengerController(PassengerCountService passengerCountService) {
        this.passengerCountService = passengerCountService;
    }
    
    @GetMapping
    public List<PassengerCount> getAllPassengerCounts() {
        return passengerCountService.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PassengerCount> getPassengerCountById(@PathVariable Long id) {
        PassengerCount passengerCount = passengerCountService.getById(id);
        return passengerCount != null ? ResponseEntity.ok(passengerCount) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<PassengerCount> createPassengerCount(@RequestBody PassengerCount passengerCount) {
        PassengerCount created = passengerCountService.create(passengerCount);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/stop/{stopId}")
    public List<PassengerCount> getByStop(@PathVariable Long stopId) {
        return passengerCountService.getByStopId(stopId);
    }
    
    @GetMapping("/bus/{busId}")
    public List<PassengerCount> getByBus(@PathVariable Long busId) {
        return passengerCountService.getByBusId(busId);
    }
    
    @GetMapping("/route/{routeId}")
    public List<PassengerCount> getByRoute(@PathVariable Long routeId) {
        return passengerCountService.getByRouteId(routeId);
    }
}