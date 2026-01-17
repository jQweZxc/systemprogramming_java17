package com.example.demo.controller;

import com.example.demo.dto.PassengerResponseDto;
import com.example.demo.model.PassengerCount;
import com.example.demo.service.PassengerCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {
    
    private final PassengerCountService passengerCountService;
    
    // === Чтение (возвращаем DTO) ===
    
    //@PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping
    public ResponseEntity<List<PassengerResponseDto>> getAllPassengerCounts() {
        try {
            List<PassengerResponseDto> passengers = passengerCountService.getAll();
            log.info("Returning {} passengers", passengers.size());
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            log.error("Error getting all passengers", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    //@PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> getPassengerCountById(@PathVariable Long id) {
        try {
            PassengerResponseDto passenger = passengerCountService.getById(id);
            return ResponseEntity.ok(passenger);
        } catch (RuntimeException e) {
            log.warn("Passenger not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting passenger with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    //@PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/stop/{stopId}")
    public ResponseEntity<List<PassengerResponseDto>> getByStop(@PathVariable Long stopId) {
        try {
            List<PassengerResponseDto> passengers = passengerCountService.getByStopId(stopId);
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            log.error("Error getting passengers by stop id: {}", stopId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    //@PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<PassengerResponseDto>> getByBus(@PathVariable Long busId) {
        try {
            List<PassengerResponseDto> passengers = passengerCountService.getByBusId(busId);
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            log.error("Error getting passengers by bus id: {}", busId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    //@PreAuthorize("hasAuthority('PASSENGER_READ')")
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<PassengerResponseDto>> getByRoute(@PathVariable Long routeId) {
        try {
            List<PassengerResponseDto> passengers = passengerCountService.getByRouteId(routeId);
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            log.error("Error getting passengers by route id: {}", routeId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // === Создание/Обновление/Удаление (работаем с сущностями) ===
    
    //@PreAuthorize("hasAuthority('PASSENGER_CREATE')")
    @PostMapping
    public ResponseEntity<PassengerCount> createPassengerCount(@RequestBody PassengerCount passengerCount) {
        try {
            PassengerCount created = passengerCountService.create(passengerCount);
            log.info("Created passenger with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Error creating passenger", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    //@PreAuthorize("hasAuthority('PASSENGER_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<PassengerCount> updatePassengerCount(@PathVariable Long id, @RequestBody PassengerCount passengerCount) {
        try {
            PassengerCount updated = passengerCountService.update(id, passengerCount);
            log.info("Updated passenger with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Passenger not found for update with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating passenger with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    //@PreAuthorize("hasAuthority('PASSENGER_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassengerCount(@PathVariable Long id) {
        try {
            boolean deleted = passengerCountService.delete(id);
            if (deleted) {
                log.info("Deleted passenger with id: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Passenger not found for deletion with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting passenger with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // === Дополнительные endpoints ===
    
    /**
     * Получить статистику по остановке за период
     * Формат дат: YYYY-MM-DDTHH:mm:ss
     */
    @GetMapping("/stats/stop/{stopId}")
    public ResponseEntity<Object[]> getStatsByStopAndPeriod(
            @PathVariable Long stopId,
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(start);
            LocalDateTime endDate = LocalDateTime.parse(end);
            
            Object[] stats = passengerCountService.getStatsByStopAndPeriod(stopId, startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting stats for stop id: {}", stopId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}