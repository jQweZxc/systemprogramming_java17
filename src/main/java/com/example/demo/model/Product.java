package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "ПРОДУКТ" - ОСНОВНАЯ БИЗНЕС-МОДЕЛЬ ПРИЛОЖЕНИЯ
 * 
 * Этот класс представляет таблицу в базе данных и используется для:
 * - Хранения данных о продуктах в PostgreSQL
 * - Валидации входящих данных через REST API
 * - Сериализации/десериализации в JSON
 */
@NoArgsConstructor      // Генерирует конструктор без аргументов (требуется для JPA)
@AllArgsConstructor     // Генерирует конструктор со всеми аргументами
@Data                   // Генерирует геттеры, сеттеры, toString, equals, hashCode
@Entity                 // Помечает класс как JPA сущность (таблица в БД)
@Table(name = "products") // Указывает имя таблицы в базе данных
public class Product {

    /**
     * УНИКАЛЬНЫЙ ИДЕНТИФИКАТОР ПРОДУКТА
     * 
     * Первичный ключ таблицы, генерируется автоматически базой данных.
     * Стратегия IDENTITY означает, что БД сама назначает уникальные ID
     * (автоинкремент в PostgreSQL).
     */
    @Id // Помечает поле как первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автогенерация ID БД
    private Long id;

    /**
     * НАЗВАНИЕ ПРОДУКТА
     * 
     * Обязательное поле, должно быть уникальным в системе.
     * Подвергается строгой валидации на стороне сервера.
     */
    @Column(
        nullable = false,   // Поле не может быть NULL в базе данных
        unique = true,      // Значение должно быть уникальным в таблице
        length = 100        // Максимальная длина в базе данных (100 символов)
    )
    @NotBlank(message = "Название пустое") // Не может быть пустым или состоять из пробелов
    @Size(
        min = 2, 
        max = 100, 
        message = "Название должно быть от 2 до 100 символов"
    ) // Ограничение длины строки
    private String title;

    /**
     * СТОИМОСТЬ ПРОДУКТА
     * 
     * Целочисленное значение, представляющее цену в рублях.
     * Должна быть положительным числом.
     */
    @Column(nullable = false) // Поле не может быть NULL в базе данных
    @Min(1) // Минимальное значение = 1 (цена не может быть 0 или отрицательной)
    private int cost;
}