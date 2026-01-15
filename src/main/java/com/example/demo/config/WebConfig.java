package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Разрешаем доступ с этих origins
                .allowedOrigins(
                    "http://127.0.0.1:5500",    // VS Code Live Server
                    "http://localhost:5500",     // VS Code Live Server альтернативный
                    "http://localhost:3000",     // React/Vue dev server
                    "http://127.0.0.1:3000",     // React/Vue dev server альтернативный
                    "http://localhost:8081",     // Другой порт если нужно
                    "http://127.0.0.1:8081"      // Другой порт альтернативный
                )
                // Разрешенные HTTP методы
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD")
                // Разрешенные заголовки
                .allowedHeaders("*")
                // Разрешаем cookies/авторизацию
                .allowCredentials(true)
                // Время кэширования preflight запросов (в секундах)
                .maxAge(3600);
    }
}