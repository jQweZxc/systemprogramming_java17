package com.example.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TelegramBotService extends TelegramLongPollingBot {
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    @Value("${telegram.log.chat.id}")
    private String chatId;
    
    private boolean isConfigured = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @PostConstruct
    public void init() {
        log.info("Инициализация Telegram бота @{}", botUsername);
        
        // Проверяем токен
        if (botToken == null || botToken.trim().isEmpty() || botToken.contains("YOUR")) {
            log.error("❌ Токен бота не настроен. Проверьте application.yml");
            log.info("Токен должен быть: 8298138115:AAFqjtK0Yz68FB_8mftP-IFK7BvdslscQWI");
            return;
        }
        
        // Проверяем chatId (может быть пустым на старте)
        if (chatId == null || chatId.trim().isEmpty()) {
            log.warn("⚠️ Chat ID не настроен. Используйте /detect-chat-id API для обнаружения");
        } else {
            isConfigured = true;
            log.info("✅ Telegram бот настроен. Chat ID: {}", chatId);
            sendStartupMessage();
        }
        
        // Тестовая задача для проверки связи
        scheduler.schedule(() -> {
            if (isConfigured) {
                sendTestMessage();
            }
        }, 10, TimeUnit.SECONDS);
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Получено обновление от Telegram: {}", update);
        
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();
            
            log.info("📩 Сообщение от @{}: {}", username, messageText);
            
            // Если chatId еще не сохранен в конфигурации - сохраняем
            if (this.chatId == null || this.chatId.isEmpty()) {
                this.chatId = String.valueOf(chatId);
                isConfigured = true;
                log.info("✅ Автообнаружен Chat ID: {}", chatId);
                sendMessage(chatId, "✅ Chat ID сохранен: " + chatId + 
                    "\nСистема готова к работе!");
            }
            
            // Обработка команд
            handleCommand(chatId, messageText, username);
        }
    }
    
    private void handleCommand(long chatId, String command, String username) {
        String response;
        
        switch (command.toLowerCase()) {
            case "/start":
                response = """
                    🚌 *Система управления пассажиропотоком*
                    
                    Доступные команды:
                    /help - Справка
                    /stats - Статистика системы
                    /status - Статус сервиса
                    /report - Последний отчет
                    
                    Система будет отправлять уведомления о:
                    • 📊 Учете пассажиров
                    • ⚠️ Перегруженных автобусах
                    • 📄 Сгенерированных отчетах
                    • 👤 Действиях пользователей
                    """;
                break;
                
            case "/help":
                response = """
                    📋 *Доступные команды:*
                    
                    /start - Начало работы
                    /help - Эта справка
                    /stats - Статистика системы
                    /status - Проверка работы сервиса
                    /report - Информация о последнем отчете
                    /ping - Проверка связи
                    
                    *Автоматические уведомления:*
                    👥 - Учет пассажиров
                    📊 - Статистика
                    ⚠️ - Предупреждения
                    ✅ - Успешные операции
                    ❌ - Ошибки системы
                    """;
                break;
                
            case "/stats":
                response = generateStatsMessage();
                break;
                
            case "/status":
                response = "✅ *Статус системы:* Работает нормально\n" +
                          "🕐 *Время сервера:* " + 
                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "\n" +
                          "🤖 *Бот:* @qwe24567Bot активен";
                break;
                
            case "/ping":
                response = "🏓 Pong!\n" +
                          "Chat ID: " + chatId + "\n" +
                          "Время: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                break;
                
            case "/report":
                response = "📄 *Информация об отчетах:*\n" +
                          "Для генерации отчетов используйте веб-интерфейс.\n" +
                          "API: http://localhost:8080/api/reports/daily\n" +
                          "Все отчеты логируются в этот чат.";
                break;
                
            default:
                response = "🤔 Неизвестная команда. Используйте /help для списка команд.";
        }
        
        sendMessage(chatId, response);
    }
    
    private String generateStatsMessage() {
        return """
               📊 *Статистика системы:*
               
               *Автобусы:*
               • Всего: 4
               • На маршруте: 3
               • В ремонте: 1
               
               *Пассажиры (сегодня):*
               • Вошедшие: 1,245
               • Вышедшие: 1,180
               • Чистый прирост: 65
               
               *Маршруты:*
               • Активные: 2
               • Загруженность: 65-85%
               
               *Последнее обновление:* %s
               """.formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    @Async
    public void sendLog(String message) {
        if (!isConfigured || chatId == null) {
            log.info("[Telegram] {}", message);
            return;
        }
        
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("📝 " + message);
            sendMessage.setParseMode("HTML");
            execute(sendMessage);
            log.debug("Лог отправлен в Telegram: {}", message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки лога в Telegram: {}", e.getMessage());
        }
    }
    
    @Async
    public void sendAlert(String message) {
        if (!isConfigured || chatId == null) {
            log.warn("[Telegram Alert] {}", message);
            return;
        }
        
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("⚠️ " + message);
            sendMessage.setParseMode("HTML");
            execute(sendMessage);
            log.debug("Алерт отправлен в Telegram: {}", message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки алерта в Telegram", e);
        }
    }
    
    @Async
    public void sendSuccess(String message) {
        if (!isConfigured || chatId == null) {
            log.info("[Telegram Success] {}", message);
            return;
        }
        
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("✅ " + message);
            sendMessage.setParseMode("HTML");
            execute(sendMessage);
            log.debug("Success отправлен в Telegram: {}", message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки success в Telegram", e);
        }
    }
    
    private void sendStartupMessage() {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("""
                🚀 *Система управления пассажиропотоком запущена!*
                
                *Детали:*
                🕐 Время: %s
                📍 Сервер: localhost:8080
                📚 API Docs: http://localhost:8080/swagger-ui.html
                🤖 Бот: @%s
                
                Используйте /help для списка команд.
                """.formatted(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                    botUsername
                ));
            sendMessage.setParseMode("Markdown");
            execute(sendMessage);
            log.info("Стартовое сообщение отправлено в Telegram");
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить startup сообщение", e);
        }
    }
    
    private void sendTestMessage() {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("🔧 *Тестовое сообщение от системы*\n" +
                              "Бот работает корректно. Готов к приему уведомлений.");
            sendMessage.setParseMode("Markdown");
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка тестового сообщения", e);
        }
    }
    
    private void sendMessage(long chatId, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            message.setParseMode("Markdown");
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в Telegram", e);
        }
    }
}