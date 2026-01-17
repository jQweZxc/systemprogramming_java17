package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.TelegramBotService;
import com.example.demo.service.TelegramTestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
@Tag(name = "Telegram", description = "API для работы с Telegram ботом")
public class TelegramController {
    
    private final TelegramBotService telegramBotService;
    private final TelegramTestService telegramTestService;
    
    @Operation(summary = "Отправить тестовое сообщение")
    @PostMapping("/test")
    public ResponseEntity<String> sendTestMessage() {
        try {
            telegramTestService.sendTestMessage();
            return ResponseEntity.ok("{\"status\": \"success\", \"message\": \"Test message sent\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Отправить оповещение о проблеме")
    @PostMapping("/alert")
    public ResponseEntity<String> sendAlert(@RequestParam String message) {
        try {
            telegramTestService.sendEmergencyAlert(message);
            return ResponseEntity.ok(" Оповещение отправлено в Telegram");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(" Ошибка: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Отправить статистику")
    @PostMapping("/stats")
    public ResponseEntity<String> sendStats() {
        try {
            telegramTestService.sendSystemStats();
            return ResponseEntity.ok(" Статистика отправлена в Telegram");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(" Ошибка: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Отправить произвольное сообщение")
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        try {
            telegramBotService.sendMessage(message);
            return ResponseEntity.ok(" Сообщение отправлено в Telegram");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(" Ошибка: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Получить статус бота")
    @GetMapping("/status")
    public ResponseEntity<String> getBotStatus() {
        try {
            return ResponseEntity.ok(" Telegram бот активен и готов к работе");
        } catch (Exception e) {
            return ResponseEntity.ok(" Telegram бот временно недоступен");
        }
    }
}