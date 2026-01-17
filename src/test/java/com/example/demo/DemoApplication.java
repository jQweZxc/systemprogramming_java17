package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("=".repeat(50));
        System.out.println("🚀 Система управления пассажиропотоком запущена!");
        System.out.println("📡 API доступен по адресу: http://localhost:8080");
        System.out.println("📋 Swagger документация: http://localhost:8080/swagger-ui.html");
        System.out.println("🤖 Telegram бот: @qwe24567Bot");
        System.out.println("⏰ Время запуска: " + java.time.LocalDateTime.now());
        System.out.println("=".repeat(50));
    }
}