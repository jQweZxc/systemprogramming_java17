package com.example.demo.controller;

import com.example.demo.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    @PreAuthorize("hasAuthority('REPORT_READ')")
    @GetMapping("/daily")
    @Operation(summary = "Получить дневной отчет", description = "Генерация отчета по пассажиропотоку за указанный день")
    @ApiResponse(responseCode = "200", description = "Отчет успешно сгенерирован")
    public ResponseEntity<byte[]> getDailyReport(
            @Parameter(description = "Дата отчета", example = "2024-01-15")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        byte[] report = reportService.generateDailyPassengerReport(date);
        
        if (report.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка генерации отчета".getBytes());
        }
        
        String filename = String.format("passenger-report-%s.txt", 
            date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(report);
    }
    
    @PreAuthorize("hasAuthority('REPORT_READ')")
    @GetMapping("/daily/csv")
    @Operation(summary = "Получить CSV отчет", description = "Генерация CSV отчета по пассажиропотоку")
    public ResponseEntity<byte[]> getDailyCsvReport(
            @Parameter(description = "Дата отчета", example = "2024-01-15")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        byte[] report = reportService.generateCsvReport(date);
        
        if (report.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка генерации CSV отчета".getBytes());
        }
        
        String filename = String.format("passenger-data-%s.csv", 
            date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(report);
    }
    
    @PreAuthorize("hasAuthority('REPORT_READ')")
    @GetMapping("/stats")
    @Operation(summary = "Получить статистику", description = "Получение статистики системы для дашборда")
    public ResponseEntity<String> getSystemStats() {
        String stats = reportService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @PreAuthorize("hasAuthority('REPORT_READ')")
    @GetMapping("/telegram-summary")
    @Operation(summary = "Получить краткую статистику для Telegram")
    public ResponseEntity<String> getTelegramSummary(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        String summary = reportService.generateTelegramSummary(date);
        return ResponseEntity.ok(summary);
    }
}