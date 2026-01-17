package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.service.TelegramBotService;

import lombok.RequiredArgsConstructor;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class TelegramInitializer implements CommandLineRunner {
    
    private final TelegramBotService telegramBotService;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🤖 Инициализация Telegram бота...");
        telegramBotService.sendMessage(
            "🚀 *Система управления пассажиропотоком запущена!*\n\n" +
            "📡 Сервер: http://localhost:8080\n" +
            "📋 API: /api/*\n" +
            "📊 Мониторинг: /swagger-ui.html\n" +
            "⏰ Время запуска: " + java.time.LocalDateTime.now()
        );
        System.out.println("✅ Telegram бот инициализирован");
    }
}