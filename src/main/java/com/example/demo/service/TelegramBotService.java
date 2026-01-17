package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TelegramBotService {
    
    @Value("${telegram.bot.token:8298138115:AAFqjtK0Yz68FB_8mftP-IFK7BvdslscQWI}")
    private String botToken;
    
    @Value("${telegram.bot.username:qwe24567Bot}")
    private String botUsername;
    
    @Value("${telegram.log.chat-id:-5294378665}")
    private String logChatId;
    
    private TelegramBot bot;
    
    @PostConstruct
    public void init() {
        try {
            bot = new TelegramBot();
            log.info("✅ Telegram бот инициализирован: @{}", botUsername);
            sendMessage("🚀 Система управления пассажиропотоком запущена");
        } catch (Exception e) {
            log.warn("⚠️ Telegram бот не инициализирован: {}", e.getMessage());
        }
    }
    
    private void sendSimpleMessage(String message) {
    try {
        if (bot == null) return;
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(logChatId);
        sendMessage.setText(message);
        sendMessage.disableWebPagePreview();
        
        bot.execute(sendMessage);
        log.info("📨 Telegram сообщение отправлено");
    } catch (TelegramApiException e) {
        log.error("❌ Ошибка отправки Telegram сообщения: {}", e.getMessage());
    }
    }
    // Основной метод отправки сообщений
    public void sendMessage(String message) {
        sendToChat(logChatId, message);
    }
    
    // Алиасы для обратной совместимости
    public void sendLog(String message) {
        sendMessage("📝 ЛОГ: " + message);
    }
    
    public void sendAlert(String message) {
        sendMessage("🚨 ОПОВЕЩЕНИЕ: " + message);
    }
    
    public void sendFormattedMessage(String message) {
        String formatted = "📊 *Система управления пассажиропотоком*\n\n" + 
                          message + "\n\n" +
                          "⏰ " + java.time.LocalDateTime.now();
        sendMessage(formatted);
    }
    
    public void sendMessageToAdmin(String message) {
        sendMessage(message);
    }
    
    // Приватный метод для отправки в конкретный чат
    private void sendToChat(String chatId, String message) {
        try {
            if (bot == null) {
                log.warn("Telegram бот не инициализирован, сообщение: {}", message);
                return;
            }
            
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage.enableMarkdown(true);
            
            bot.execute(sendMessage);
            log.info("📨 Telegram сообщение отправлено: {}", message);
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка отправки Telegram сообщения: {}", e.getMessage());
        }
    }
    
    private class TelegramBot extends TelegramLongPollingBot {
        
        public TelegramBot() {
            super(new DefaultBotOptions(), botToken);
        }
        
        @Override
        public String getBotUsername() {
            return botUsername;
        }
        
        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                
                log.info("📩 Получено сообщение от {}: {}", chatId, messageText);
                
                // Ответ на команды
                if (messageText.equals("/start") || messageText.equals("/help")) {
                    sendResponse(chatId, 
                        "👋 Привет! Я бот системы управления пассажиропотоком.\n\n" +
                        "📋 Доступные команды:\n" +
                        "/start - Начало работы\n" +
                        "/status - Статус системы\n" +
                        "/help - Помощь\n\n" +
                        "Я буду отправлять уведомления о работе системы.");
                } else if (messageText.equals("/status")) {
                    String status = "✅ Система работает нормально\n" +
                                   "⏰ Время сервера: " + java.time.LocalDateTime.now() + "\n" +
                                   "📡 API: http://localhost:8080\n" +
                                   "📱 Версия: 1.0.0";
                    sendResponse(chatId, status);
                } else {
                    sendResponse(chatId, "🤖 Я пока умею только отправлять уведомления. Для списка команд используйте /help");
                }
            }
        }
        
        private void sendResponse(Long chatId, String text) {
            try {
                SendMessage message = new SendMessage();
                message.setChatId(chatId.toString());
                message.setText(text);
                message.enableMarkdown(true);
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Ошибка отправки ответа: {}", e.getMessage());
            }
        }
    }
}