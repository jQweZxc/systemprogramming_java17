package com.example.demo.service;

import com.example.demo.model.Stop;
import com.example.demo.repository.StopRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StopService {
    
    private final StopRepository stopRepository;
    
    public StopService(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }
    
    public List<Stop> getAll() {
        return stopRepository.findAll();
    }
    
    public Stop getById(Long id) {
        return stopRepository.findById(id).orElse(null);
    }
    
    public Stop create(Stop stop) {
        return stopRepository.save(stop);
    }
    
    public List<Stop> getNearbyStops(Double lat, Double lon) {
        return stopRepository.findNearbyStops(lat, lon);
    }
}