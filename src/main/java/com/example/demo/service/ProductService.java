package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.Specification.ProductSpecification;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

@Service // Помечает класс как Spring Bean сервисного слоя
public class ProductService {
    
    // Внедрение зависимости репозитория для работы с БД
    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Cacheable(value = "products", key = "#root.methodName")
    public List<Product> getAll() {
        return productRepository.findAll();
    }


    @Cacheable(value = "product", key = "#id")
    public Product getById(Long id) {
        // orElse(null) - возвращает null если продукт не найден
        return productRepository.findById(id).orElse(null);
    }


    public Product create(Product product) {
        return productRepository.save(product);
    }


    public Product updateById(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    // Обновляем поля существующего продукта
                    product.setTitle(updatedProduct.getTitle());
                    product.setCost(updatedProduct.getCost());
                    // Сохраняем обновленный продукт
                    return productRepository.save(product);
                })
                .orElse(null); // Возвращаем null если продукт не найден
    }


    public boolean deleteById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public List<Product> getByTitle(String title) {
        if (title != null) {
            // Поиск с игнорированием регистра (ContainingIgnoreCase)
            return productRepository.findByTitleContainingIgnoreCase(title);
        }
        // Если title не указан - возвращаем все продукты
        return productRepository.findAll();
    } 
    

    public Page<Product> getByFilter(String title, Integer min, Integer max, Pageable pageable) {
        return productRepository.findAll(
            // Создаем спецификацию для фильтрации
            ProductSpecification.filter(title, min, max),
            pageable); // Применяем пагинацию
    }
}