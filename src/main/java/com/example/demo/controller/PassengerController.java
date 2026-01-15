package com.example.demo.controller;

import com.example.demo.model.PassengerCount;
import com.example.demo.service.PassengerCountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    
    private final PassengerCountService passengerCountService;
    
    public PassengerController(PassengerCountService passengerCountService) {
        this.passengerCountService = passengerCountService;
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping
    public List<PassengerCount> getAllPassengerCounts() {
        return passengerCountService.getAll();
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<PassengerCount> getPassengerCountById(@PathVariable Long id) {
        PassengerCount passengerCount = passengerCountService.getById(id);
        return passengerCount != null ? ResponseEntity.ok(passengerCount) : ResponseEntity.notFound().build();
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_CREATE')")
    @PostMapping
    public ResponseEntity<PassengerCount> createPassengerCount(@RequestBody PassengerCount passengerCount) {
        PassengerCount created = passengerCountService.create(passengerCount);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<PassengerCount> updatePassengerCount(@PathVariable Long id, @RequestBody PassengerCount passengerCount) {
        PassengerCount updated = passengerCountService.update(id, passengerCount);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassengerCount(@PathVariable Long id) {
        boolean deleted = passengerCountService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/stop/{stopId}")
    public List<PassengerCount> getByStop(@PathVariable Long stopId) {
        return passengerCountService.getByStopId(stopId);
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/bus/{busId}")
    public List<PassengerCount> getByBus(@PathVariable Long busId) {
        return passengerCountService.getByBusId(busId);
    }
    
    @PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/route/{routeId}")
    public List<PassengerCount> getByRoute(@PathVariable Long routeId) {
        return passengerCountService.getByRouteId(routeId);
    }
}