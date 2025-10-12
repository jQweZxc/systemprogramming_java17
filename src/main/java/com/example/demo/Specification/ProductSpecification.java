package com.example.demo.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.model.Product;

/**
 * СПЕЦИФИКАЦИИ ДЛЯ РАСШИРЕННОЙ ФИЛЬТРАЦИИ ПРОДУКТОВ
 * 
 * Этот класс реализует паттерн "Specification" для построения
 * динамических SQL запросов к базе данных на основе критериев фильтрации.
 * 
 * Использует JPA Criteria API для типобезопасных запросов.
 */
public class ProductSpecification {
    
    /**
     * СПЕЦИФИКАЦИЯ ДЛЯ ПОИСКА ПО НАЗВАНИЮ (С УЧЕТОМ РЕГИСТРА)
     * 
     * Создает условие LIKE для поиска по части названия.
     * Поиск нечувствителен к регистру (преобразует в нижний регистр).
     * 
     * Пример: поиск "mer" найдет "Mercedes", "мерседес", "MERCEDES"
     * 
     * @param title - искомая строка в названии
     * @return Specification<Product> - спецификация для JPA запроса
     */
    private static Specification<Product> titlelike(String title) {
        return (root, query, cB) -> {
            // Проверяем что строка не null и не пустая
            if (title == null || title.trim().isEmpty()) {
                return null; // Возвращаем null - условие не добавляется в запрос
            }
            
            // Создаем условие LIKE с учетом регистра:
            // cB.lower() - преобразует в нижний регистр
            // "%"+...+"%" - ищет вхождение в любой части строки
            return cB.like(
                cB.lower(root.get("title")), 
                "%" + title.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     * СПЕЦИФИКАЦИЯ ДЛЯ ФИЛЬТРАЦИИ ПО ЦЕНЕ (ДИАПАЗОН)
     * 
     * Поддерживает различные варианты фильтрации по цене:
     * - Диапазон (min и max)
     * - Минимальная цена (только min)
     * - Максимальная цена (только max)
     * 
     * @param min - минимальная цена (включительно)
     * @param max - максимальная цена (включительно)
     * @return Specification<Product> - спецификация для фильтрации по цене
     */
    private static Specification<Product> priceBetween(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            // Все параметры null - фильтрация не нужна
            if (min == null && max == null) {
                return null;
            } 
            // Оба параметра заданы - диапазон цен
            else if (min != null && max != null) {
                return criteriaBuilder.between(root.get("cost"), min, max);
            } 
            // Только минимальная цена задана
            else if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), min);
            } 
            // Только максимальная цена задана
            else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("cost"), max);
            }
        };
    }

    /**
     * ОСНОВНОЙ МЕТОД ДЛЯ СОЗДАНИЯ КОМБИНИРОВАННОЙ ФИЛЬТРАЦИИ
     * 
     * Объединяет все условия фильтрации с оператором AND.
     * Условия, возвращающие null, игнорируются.
     * 
     * @param title - фильтр по названию
     * @param min   - минимальная цена
     * @param max   - максимальная цена
     * @return Specification<Product> - комбинированная спецификация
     */
    public static Specification<Product> filter(String title, Integer min, Integer max) {
        return Specification.allOf(
            titlelike(title), 
            priceBetween(min, max)
        );
    }
}