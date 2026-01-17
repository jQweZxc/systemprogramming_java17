package com.example.demo.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelegramTestService {
    
    private final TelegramBotService telegramBotService;
    
    /**
     * Отправка тестового сообщения
     */
    public void sendTestMessage() {
        String message = "🧪 Тестовое сообщение из системы\n" +
                        "✅ Проверка работы Telegram бота\n" +
                        "⏰ Время: " + java.time.LocalDateTime.now();
        
        telegramBotService.sendMessage(message);
    }
    
    /**
     * Отправка статистики системы
     */
    public void sendSystemStats() {
        String stats = "📊 Статистика системы:\n" +
                      "• 🚌 Автобусов: 15\n" +
                      "• 🗺️ Остановок: 42\n" +
                      "• 👥 Пассажиров сегодня: 1250\n" +
                      "• 📈 Загруженность: 72%\n" +
                      "• ⚡ Статус: Все системы работают";
        
        telegramBotService.sendFormattedMessage(stats);
    }
    
    /**
     * Отправка экстренного оповещения
     */
    public void sendEmergencyAlert(String alertMessage) {
        String message = "🚨 ЭКСТРЕННОЕ ОПОВЕЩЕНИЕ:\n" + alertMessage;
        telegramBotService.sendAlert(message);
    }
}