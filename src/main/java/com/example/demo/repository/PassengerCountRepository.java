package com.example.demo.repository;

import com.example.demo.model.PassengerCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PassengerCountRepository extends JpaRepository<PassengerCount, Long> {
    
    // Поиск по остановке
    List<PassengerCount> findByStopId(Long stopId);
    
    // Поиск по автобусу
    List<PassengerCount> findByBusId(Long busId);
    
    // Поиск по маршруту через автобус
    @Query("SELECT pc FROM PassengerCount pc WHERE pc.bus.route.id = :routeId")
    List<PassengerCount> findByRouteId(@Param("routeId") Long routeId);
    
    // Поиск по временному диапазону
    List<PassengerCount> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    // Статистика по остановке за период
    @Query("SELECT SUM(pc.entered), SUM(pc.exited) FROM PassengerCount pc WHERE pc.stop.id = :stopId AND pc.timestamp BETWEEN :start AND :end")
    Object[] findPassengerStatsByStopAndPeriod(@Param("stopId") Long stopId, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end);
}