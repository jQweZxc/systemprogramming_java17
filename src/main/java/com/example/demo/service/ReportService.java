// src/main/java/com/example/demo/service/ReportService.java
package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final PassengerCountRepository passengerCountRepository;
    private final BusRepository busRepository;
    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    
    /**
     * Генерация отчета по пассажиропотоку за день
     */
    public byte[] generateDailyPassengerReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        
        List<PassengerCount> passengers = passengerCountRepository
            .findByTimestampBetween(start, end);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // Заголовок отчета
            writer.println("=".repeat(60));
            writer.printf("ОТЧЕТ ПО ПАССАЖИРОПОТОКУ\n");
            writer.printf("Дата: %s\n", date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            writer.println("=".repeat(60));
            writer.println();
            
            // Статистика
            int totalEntered = passengers.stream()
                .mapToInt(PassengerCount::getEntered)
                .sum();
            int totalExited = passengers.stream()
                .mapToInt(PassengerCount::getExited)
                .sum();
            int netChange = totalEntered - totalExited;
            
            writer.println("ОБЩАЯ СТАТИСТИКА:");
            writer.printf("  Всего вошедших: %,d чел.\n", totalEntered);
            writer.printf("  Всего вышедших: %,d чел.\n", totalExited);
            writer.printf("  Чистое изменение: %,d чел.\n", netChange);
            writer.printf("  Всего записей: %,d\n", passengers.size());
            writer.println();
            
            // По остановкам
            writer.println("СТАТИСТИКА ПО ОСТАНОВКАМ:");
            writer.println("-".repeat(60));
            writer.printf("%-20s %-12s %-12s %-12s\n", 
                "Остановка", "Вошедшие", "Вышедшие", "Нагрузка");
            writer.println("-".repeat(60));
            
            List<Stop> allStops = stopRepository.findAll();
            for (Stop stop : allStops) {
                List<PassengerCount> stopPassengers = passengers.stream()
                    .filter(p -> p.getStop() != null && p.getStop().getId().equals(stop.getId()))
                    .toList();
                
                int stopEntered = stopPassengers.stream()
                    .mapToInt(PassengerCount::getEntered)
                    .sum();
                int stopExited = stopPassengers.stream()
                    .mapToInt(PassengerCount::getExited)
                    .sum();
                
                if (stopEntered > 0 || stopExited > 0) {
                    writer.printf("%-20s %,12d %,12d %,12d\n",
                        stop.getName(), stopEntered, stopExited, stopEntered - stopExited);
                }
            }
            
            writer.println();
            
            // По автобусам
            writer.println("СТАТИСТИКА ПО АВТОБУСАМ:");
            writer.println("-".repeat(60));
            writer.printf("%-15s %-20s %-12s\n", 
                "Автобус", "Маршрут", "Пассажиров");
            writer.println("-".repeat(60));
            
            List<Bus> allBuses = busRepository.findAll();
            for (Bus bus : allBuses) {
                List<PassengerCount> busPassengers = passengers.stream()
                    .filter(p -> p.getBus() != null && p.getBus().getId().equals(bus.getId()))
                    .toList();
                
                int busPassengerCount = busPassengers.stream()
                    .mapToInt(p -> p.getEntered() - p.getExited())
                    .sum();
                
                if (busPassengerCount != 0) {
                    String routeInfo = bus.getRoute() != null ? 
                        "Маршрут " + bus.getRoute().getId() : "Не назначен";
                    writer.printf("%-15s %-20s %,12d\n",
                        bus.getModel(), routeInfo, busPassengerCount);
                }
            }
            
            writer.println();
            writer.printf("Отчет сгенерирован: %s\n", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            writer.println("=".repeat(60));
            
            writer.flush();
            
            log.info("Сгенерирован дневной отчет за {}: {} записей", date, passengers.size());
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Ошибка генерации отчета за {}", date, e);
            return ("Ошибка генерации отчета: " + e.getMessage()).getBytes();
        }
    }
    
    /**
     * Генерация CSV отчета для импорта
     */
    public byte[] generateCsvReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        
        List<PassengerCount> passengers = passengerCountRepository
            .findByTimestampBetween(start, end);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV заголовок
            writer.println("timestamp,bus_id,stop_id,entered,exited,net_change");
            
            // Данные
            for (PassengerCount passenger : passengers) {
                int netChange = passenger.getEntered() - passenger.getExited();
                Long busId = passenger.getBus() != null ? passenger.getBus().getId() : null;
                Long stopId = passenger.getStop() != null ? passenger.getStop().getId() : null;
                
                writer.printf("%s,%s,%s,%d,%d,%d\n",
                    passenger.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    busId != null ? busId : "",
                    stopId != null ? stopId : "",
                    passenger.getEntered(),
                    passenger.getExited(),
                    netChange
                );
            }
            
            writer.flush();
            
            log.info("Сгенерирован CSV отчет за {}: {} записей", date, passengers.size());
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Ошибка генерации CSV отчета за {}", date, e);
            return ("Ошибка генерации CSV отчета: " + e.getMessage()).getBytes();
        }
    }
    
    /**
     * Статистика для дашборда
     */
    public String getDashboardStats() {
        long totalBuses = busRepository.count();
        long totalStops = stopRepository.count();
        long totalRoutes = routeRepository.count();
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        
        List<PassengerCount> todayPassengers = passengerCountRepository
            .findByTimestampBetween(todayStart, todayEnd);
        
        int todayPassengersCount = todayPassengers.stream()
            .mapToInt(p -> p.getEntered() + p.getExited())
            .sum();
        
        // Средняя загруженность автобусов
        String avgLoad = "N/A";
        if (totalBuses > 0) {
            double avg = todayPassengersCount / (double) totalBuses;
            avgLoad = String.format("%.1f", avg);
        }
        
        String stats = String.format(
            "📊 Статистика системы:\n" +
            "• Автобусов: %d\n" +
            "• Остановок: %d\n" +
            "• Маршрутов: %d\n" +
            "• Пассажиров сегодня: %,d чел.\n" +
            "• Средняя загруженность: %s чел./автобус",
            totalBuses, totalStops, totalRoutes, todayPassengersCount, avgLoad
        );
        
        log.info("Получена статистика системы");
        return stats;
    }
    
    /**
     * Простой текстовый отчет для Telegram
     */
    public String generateTelegramSummary(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        
        List<PassengerCount> passengers = passengerCountRepository
            .findByTimestampBetween(start, end);
        
        int totalEntered = passengers.stream()
            .mapToInt(PassengerCount::getEntered)
            .sum();
        int totalExited = passengers.stream()
            .mapToInt(PassengerCount::getExited)
            .sum();
        
        return String.format(
            "📊 Отчет за %s:\n" +
            "📈 Вошедшие: %,d чел.\n" +
            "📉 Вышедшие: %,d чел.\n" +
            "🔄 Чистое изменение: %,d чел.\n" +
            "📝 Всего записей: %,d",
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            totalEntered, totalExited, totalEntered - totalExited, passengers.size()
        );
    }
}