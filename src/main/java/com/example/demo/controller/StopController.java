package com.example.demo.controller;

import com.example.demo.model.Stop;
import com.example.demo.service.StopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stops")
public class StopController {
    
    private final StopService stopService;
    
    public StopController(StopService stopService) {
        this.stopService = stopService;
    }
    
    @GetMapping
    public List<Stop> getAllStops() {
        return stopService.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Stop> getStopById(@PathVariable Long id) {
        Stop stop = stopService.getById(id);
        return stop != null ? ResponseEntity.ok(stop) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Stop> createStop(@RequestBody Stop stop) {
        Stop createdStop = stopService.create(stop);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStop);
    }
    
    @GetMapping("/nearby")
    public List<Stop> getNearbyStops(@RequestParam Double lat, @RequestParam Double lon) {
        return stopService.getNearbyStops(lat, lon);
    }
}