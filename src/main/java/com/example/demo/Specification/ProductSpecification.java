package com.example.demo.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.model.Product;


public class ProductSpecification {
    
    private static Specification<Product> titlelike(String title) {
        return (root, query, cB) -> {
            // Проверяем что строка не null и не пустая
            if (title == null || title.trim().isEmpty()) {
                return null; // Возвращаем null - условие не добавляется в запрос
            }
            
            return cB.like(
                cB.lower(root.get("title")), 
                "%" + title.toLowerCase().trim() + "%"
            );
        };
    }


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


    public static Specification<Product> filter(String title, Integer min, Integer max) {
        return Specification.allOf(
            titlelike(title), 
            priceBetween(min, max)
        );
    }
}