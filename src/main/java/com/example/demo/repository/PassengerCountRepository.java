package com.example.demo.repository;

import com.example.demo.dto.PassengerResponseDto;
import com.example.demo.model.PassengerCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerCountRepository extends JpaRepository<PassengerCount, Long> {
    
    // Поиск по остановке
    List<PassengerCount> findByStopId(Long stopId);
    
    // Поиск по автобусу
    List<PassengerCount> findByBusId(Long busId);
    
    // Поиск по маршруту через автобус
    @Query("SELECT pc FROM PassengerCount pc WHERE pc.bus.route.id = :routeId")
    List<PassengerCount> findByRouteId(@Param("routeId") Long routeId);
    
    // Все пассажиры как DTO
    @Query("SELECT new com.example.demo.dto.PassengerResponseDto(" +
           "p.id, p.timestamp, " +
           "b.id, b.model, " +
           "s.id, s.name, " +
           "p.entered, p.exited) " +
           "FROM PassengerCount p " +
           "LEFT JOIN p.bus b " +
           "LEFT JOIN p.stop s " +
           "ORDER BY p.timestamp DESC")
    List<PassengerResponseDto> findAllAsDto();
    
    // Поиск по ID как DTO
    @Query("SELECT new com.example.demo.dto.PassengerResponseDto(" +
           "p.id, p.timestamp, " +
           "b.id, b.model, " +
           "s.id, s.name, " +
           "p.entered, p.exited) " +
           "FROM PassengerCount p " +
           "LEFT JOIN p.bus b " +
           "LEFT JOIN p.stop s " +
           "WHERE p.id = :id")
    Optional<PassengerResponseDto> findDtoById(@Param("id") Long id);
    
    // По остановке как DTO
    @Query("SELECT new com.example.demo.dto.PassengerResponseDto(" +
           "p.id, p.timestamp, " +
           "b.id, b.model, " +
           "s.id, s.name, " +
           "p.entered, p.exited) " +
           "FROM PassengerCount p " +
           "LEFT JOIN p.bus b " +
           "LEFT JOIN p.stop s " +
           "WHERE p.stop.id = :stopId " +
           "ORDER BY p.timestamp DESC")
    List<PassengerResponseDto> findByStopIdAsDto(@Param("stopId") Long stopId);
    
    // По автобусу как DTO
    @Query("SELECT new com.example.demo.dto.PassengerResponseDto(" +
           "p.id, p.timestamp, " +
           "b.id, b.model, " +
           "s.id, s.name, " +
           "p.entered, p.exited) " +
           "FROM PassengerCount p " +
           "LEFT JOIN p.bus b " +
           "LEFT JOIN p.stop s " +
           "WHERE p.bus.id = :busId " +
           "ORDER BY p.timestamp DESC")
    List<PassengerResponseDto> findByBusIdAsDto(@Param("busId") Long busId);
    
    // По маршруту как DTO
    @Query("SELECT new com.example.demo.dto.PassengerResponseDto(" +
           "p.id, p.timestamp, " +
           "b.id, b.model, " +
           "s.id, s.name, " +
           "p.entered, p.exited) " +
           "FROM PassengerCount p " +
           "LEFT JOIN p.bus b " +
           "LEFT JOIN p.stop s " +
           "WHERE b.route.id = :routeId " +
           "ORDER BY p.timestamp DESC")
    List<PassengerResponseDto> findByRouteIdAsDto(@Param("routeId") Long routeId);
    
    // Поиск по временному диапазону
    List<PassengerCount> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    // Статистика по остановке за период
    @Query("SELECT SUM(pc.entered), SUM(pc.exited) FROM PassengerCount pc WHERE pc.stop.id = :stopId AND pc.timestamp BETWEEN :start AND :end")
    Object[] findPassengerStatsByStopAndPeriod(@Param("stopId") Long stopId, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end);
}