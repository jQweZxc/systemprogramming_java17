package com.example.demo.repository;

import com.example.demo.model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    
    // Поиск по названию
    List<Stop> findByNameContainingIgnoreCase(String name);
    
    // Поиск ближайших остановок по координатам (в радиусе 2 км)
    @Query("SELECT s FROM Stop s WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(s.lat)) * " +
           "cos(radians(s.lon) - radians(:lon)) + sin(radians(:lat)) * sin(radians(s.lat))) < 2")
    List<Stop> findNearbyStops(@Param("lat") Double lat, @Param("lon") Double lon);
}