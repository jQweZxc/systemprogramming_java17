package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ГЛАВНЫЙ КОНТРОЛЛЕР ПРИЛОЖЕНИЯ
 * 
 * Содержит основные endpoint'ы для главной страницы и проверки здоровья приложения
 */
@RestController
public class MainController {

    /**
     * ГЛАВНАЯ СТРАНИЦА ПРИЛОЖЕНИЯ
     * GET /
     * 
     * @return HTML страница с навигацией по системе
     */
    @GetMapping("/")
    public String home() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>🚌 Passenger Flow Management System</title>
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        margin: 40px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        min-height: 100vh;
                    }
                    .container { 
                        max-width: 800px; 
                        margin: 0 auto;
                        text-align: center;
                    }
                    .btn { 
                        display: inline-block; 
                        padding: 12px 24px; 
                        margin: 10px; 
                        background: rgba(255,255,255,0.2); 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 8px;
                        border: 2px solid white;
                        transition: all 0.3s;
                    }
                    .btn:hover {
                        background: white;
                        color: #667eea;
                    }
                    h1 { font-size: 2.5em; margin-bottom: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🚌 Система управления пассажиропотоком</h1>
                    <p>Система для мониторинга и прогнозирования загруженности общественного транспорта</p>
                    <div>
                        <a class="btn" href="/index.html">📊 Веб-интерфейс</a>
                        <a class="btn" href="/swagger-ui.html">📚 Swagger API</a>
                        <a class="btn" href="/api/health">🔍 Проверка здоровья</a>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * ПРОВЕРКА ЗДОРОВЬЯ ПРИЛОЖЕНИЯ
     * GET /api/health
     * 
     * @return JSON с статусом приложения
     */
    @GetMapping("/api/health")
    public String health() {
        return """
            {
                "status": "UP",
                "service": "Passenger Flow Management System",
                "timestamp": "%s",
                "version": "1.0.0"
            }
            """.formatted(java.time.LocalDateTime.now());
    }
}