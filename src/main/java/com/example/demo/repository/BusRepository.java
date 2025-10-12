package com.example.demo.repository;

import com.example.demo.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    
    // Поиск автобусов по маршруту
    List<Bus> findByRouteId(Long routeId);
    
    // Поиск по модели
    List<Bus> findByModelContainingIgnoreCase(String model);
}