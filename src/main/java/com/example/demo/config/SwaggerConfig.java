package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * КОНФИГУРАЦИЯ SWAGGER/OPENAPI ДОКУМЕНТАЦИИ
 * 
 * Настраивает автоматическую генерацию документации REST API
 * Документация будет доступна по адресу: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    /**
     * НАСТРОЙКА OPENAPI ДОКУМЕНТАЦИИ
     * 
     * Создает основную информацию о API для отображения в Swagger UI
     */
    @Bean
    public OpenAPI passengerFlowOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Сервер разработки");

        Contact contact = new Contact();
        contact.setName("Команда разработки");
        contact.setEmail("support@passengerflow.com");

        Info info = new Info()
                .title("🚌 Passenger Flow Management System API")
                .version("1.0.0")
                .description("""
                    REST API для системы управления пассажиропотоком.
                    
                    ## Основные возможности:
                    - 📊 Учет пассажиропотока на остановках
                    - 🔮 Прогнозирование загруженности маршрутов  
                    - 📍 Поиск ближайших остановок
                    - 📈 Статистика и аналитика
                    
                    ## Для мобильных приложений:
                    - Получение прогнозов загруженности
                    - Поиск ближайших остановок
                    - Информация о маршрутах
                    """)
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}