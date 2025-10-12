package com.example.demo.service;

import com.example.demo.model.Bus;
import com.example.demo.repository.BusRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BusService {
    
    private final BusRepository busRepository;
    
    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }
    
    public List<Bus> getAll() {
        return busRepository.findAll();
    }
    
    public Bus getById(Long id) {
        return busRepository.findById(id).orElse(null);
    }
    
    public Bus create(Bus bus) {
        return busRepository.save(bus);
    }
    
    public List<Bus> getByRouteId(Long routeId) {
        return busRepository.findByRouteId(routeId);
    }
}