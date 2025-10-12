package com.example.demo.config;

/**
 * КАСТОМНОЕ ИСКЛЮЧЕНИЕ "РЕСУРС НЕ НАЙДЕН"
 * 
 * Используется когда запрашиваемый ресурс (продукт, остановка, маршрут) не существует
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s с ID %d не найден", resourceName, id));
    }
    
    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s '%s' не найден", resourceName, identifier));
    }
}