// src/main/java/com/example/demo/service/TelegramTestService.java
package com.example.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramTestService {
    
    private final TelegramLoggingService telegramLoggingService;
    private final TelegramBotService telegramBotService;
    
    @PostConstruct
    public void testTelegramIntegration() {
        log.info("🚀 Начинаем тестирование Telegram интеграции...");
        
        try {
            // Тест 1: Отправка тестового сообщения
            telegramBotService.sendLog("🔧 Тестовое сообщение от системы управления пассажиропотоком");
            
            // Тест 2: Имитация входа пользователя
            telegramLoggingService.logUserLogin("admin", "ADMIN");
            
            // Тест 3: Имитация записи пассажиропотока
            Thread.sleep(1000);
            telegramLoggingService.logCrudOperation("PassengerCount", "CREATE", 
                "Остановка: Центральная, Вошедшие: 15, Вышедшие: 8");
            
            // Тест 4: Имитация перегруза автобуса
            Thread.sleep(1000);
            telegramLoggingService.logBusOverload("Mercedes Sprinter", 85);
            
            // Тест 5: Имитация генерации отчета
            Thread.sleep(1000);
            telegramLoggingService.logReportGenerated("Дневной отчет", 
                "report-2024-01-15.txt", "Общее количество пассажиров: 1,245");
            
            log.info("✅ Тестовые сообщения отправлены в Telegram чат");
            log.info("👈 Проверьте группу 'chatbot' в Telegram");
            
        } catch (Exception e) {
            log.error("❌ Ошибка тестирования Telegram: {}", e.getMessage());
        }
    }
}