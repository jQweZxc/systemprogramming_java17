package com.example.demo.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramLoggingService {
    
    private final TelegramBotService telegramBotService;
    
    /**
     * Логирование успешных операций
     */
    public void logSuccess(String operation, String details) {
        String message = String.format("✅ %s\n📝 %s", operation, details);
        telegramBotService.sendMessage(message);
        log.info("Telegram log (Success): {} - {}", operation, details);
    }
    
    /**
     * Логирование ошибок
     */
    public void logError(String operation, String error) {
        String message = String.format("❌ %s\n💥 Ошибка: %s", operation, error);
        telegramBotService.sendMessage(message);
        log.error("Telegram log (Error): {} - {}", operation, error);
    }
    
    /**
     * Логирование предупреждений
     */
    public void logWarning(String operation, String warning) {
        String message = String.format("⚠️ %s\n📢 Предупреждение: %s", operation, warning);
        telegramBotService.sendMessage(message);
        log.warn("Telegram log (Warning): {} - {}", operation, warning);
    }
    
    /**
     * Логирование информационных сообщений
     */
    public void logInfo(String operation, String info) {
        String message = String.format("ℹ️ %s\n📋 %s", operation, info);
        telegramBotService.sendMessage(message);
        log.info("Telegram log (Info): {} - {}", operation, info);
    }
    
    /**
     * Логирование создания новых записей
     */
    public void logCreate(String entity, Long id, String details) {
        String message = String.format("🆕 Создан %s #%d\n%s", entity, id, details);
        telegramBotService.sendMessage(message);
        log.info("Telegram log (Create): {} #{} - {}", entity, id, details);
    }
    
    /**
     * Логирование обновления записей
     */
    public void logUpdate(String entity, Long id, String details) {
        String message = String.format("✏️ Обновлен %s #%d\n%s", entity, id, details);
        telegramBotService.sendMessage(message);
        log.info("Telegram log (Update): {} #{} - {}", entity, id, details);
    }
    
    /**
     * Логирование удаления записей
     */
    public void logDelete(String entity, Long id) {
        String message = String.format("🗑️ Удален %s #%d", entity, id);
        telegramBotService.sendMessage(message);
        log.info("Telegram log (Delete): {} #{}", entity, id);
    }
    
    /**
     * Логирование входа пользователя
     */
    public void logLogin(String username, boolean success) {
        String emoji = success ? "🔓" : "🔒";
        String status = success ? "успешный" : "неудачный";
        String message = String.format("%s Вход пользователя: %s\nСтатус: %s", 
            emoji, username, status);
        telegramBotService.sendMessage(message);
        log.info("Telegram log (Login): {} - {}", username, status);
    }
    
    /**
     * Логирование работы с файлами
     */
    public void logFileOperation(String operation, String filename, boolean success) {
        String emoji = success ? "📁✅" : "📁❌";
        String status = success ? "успешно" : "с ошибкой";
        String message = String.format("%s %s файла: %s\nСтатус: %s", 
            emoji, operation, filename, status);
        telegramBotService.sendMessage(message);
        log.info("Telegram log (File): {} {} - {}", operation, filename, status);
    }
}