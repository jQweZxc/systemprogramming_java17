package com.example.demo.controller;

import com.example.demo.model.Stop;
import com.example.demo.service.StopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stops")
public class StopController {
    
    private final StopService stopService;
    
    public StopController(StopService stopService) {
        this.stopService = stopService;
    }
    
    //@PreAuthorize("hasAuthority('STOP_READ')")
    @GetMapping
    public List<Stop> getAllStops() {
        return stopService.getAll();
    }
    
    //@PreAuthorize("hasAuthority('STOP_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Stop> getStopById(@PathVariable Long id) {
        Stop stop = stopService.getById(id);
        return stop != null ? ResponseEntity.ok(stop) : ResponseEntity.notFound().build();
    }
    
    //@PreAuthorize("hasAuthority('STOP_CREATE')")
    @PostMapping
    public ResponseEntity<Stop> createStop(@RequestBody Stop stop) {
        Stop createdStop = stopService.create(stop);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStop);
    }
    
    //@PreAuthorize("hasAuthority('STOP_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<Stop> updateStop(@PathVariable Long id, @RequestBody Stop stop) {
        Stop updatedStop = stopService.update(id, stop);
        return updatedStop != null ? ResponseEntity.ok(updatedStop) : ResponseEntity.notFound().build();
    }
    
    //@PreAuthorize("hasAuthority('STOP_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStop(@PathVariable Long id) {
        boolean deleted = stopService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    //@PreAuthorize("hasAuthority('STOP_READ')")
    @GetMapping("/nearby")
    public List<Stop> getNearbyStops(@RequestParam Double lat, @RequestParam Double lon) {
        return stopService.getNearbyStops(lat, lon);
    }
}