// src/main/java/com/example/demo/service/TelegramLoggingService.java
package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramLoggingService {
    
    private final TelegramBotService telegramBotService;
    
    public void logUserLogin(String username, String role) {
        String message = String.format("""
            👤 <b>Вход в систему</b>
            Пользователь: %s
            Роль: %s
            Время: %s
            """, 
            username, role, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        telegramBotService.sendLog(message);
        log.info("User login: {} ({})", username, role);
    }
    
    public void logCrudOperation(String entity, String operation, String details) {
        String message = String.format("""
            🔄 <b>CRUD операция</b>
            Сущность: %s
            Действие: %s
            Детали: %s
            Время: %s
            """,
            entity, operation, details,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        telegramBotService.sendLog(message);
        log.info("CRUD: {} {} - {}", operation, entity, details);
    }
    
    public void logBusOverload(String busModel, int loadPercentage) {
        String message = String.format("""
            ⚠️ <b>Перегруз автобуса!</b>
            Автобус: %s
            Загруженность: %d%%
            Рекомендация: Направить дополнительный транспорт
            Время: %s
            """,
            busModel, loadPercentage,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        telegramBotService.sendAlert(message);
        log.warn("Bus overload: {} - {}%", busModel, loadPercentage);
    }
    
    public void logReportGenerated(String reportType, String filename, String details) {
        String message = String.format("""
            📄 <b>Отчет сгенерирован</b>
            Тип: %s
            Файл: %s
            %s
            Время: %s
            """,
            reportType, filename, details,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        telegramBotService.sendLog(message);
        log.info("Report generated: {} - {}", reportType, filename);
    }
    
    public void logSystemError(String operation, String error) {
        String message = String.format("""
            ❌ <b>Ошибка системы</b>
            Операция: %s
            Ошибка: %s
            Время: %s
            """,
            operation, error,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        telegramBotService.sendAlert(message);
        log.error("System error: {} - {}", operation, error);
    }

    // В TelegramLoggingService.java добавьте:
    public void logDailyReportGenerated(LocalDate date, int recordCount) {
        String summary = String.format("""
            📄 <b>Дневной отчет сгенерирован</b>
            Дата: %s
            Количество записей: %,d
            Время: %s
            """,
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            recordCount,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    
        telegramBotService.sendLog(summary);
        log.info("Daily report generated for {}: {} records", date, recordCount);
    }

}