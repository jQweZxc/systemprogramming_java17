package com.example.demo.service;

import com.example.demo.model.Route;
import com.example.demo.repository.RouteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RouteService {
    
    private final RouteRepository routeRepository;
    
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }
    
    public List<Route> getAll() {
        return routeRepository.findAll();
    }
    
    public Route getById(Long id) {
        return routeRepository.findById(id).orElse(null);
    }
    
    public Route create(Route route) {
        return routeRepository.save(route);
    }
}