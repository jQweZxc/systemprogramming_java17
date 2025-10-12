package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.Specification.ProductSpecification;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

/**
 * СЕРВИСНЫЙ СЛОЙ ДЛЯ РАБОТЫ С ПРОДУКТАМИ
 * 
 * Этот класс содержит бизнес-логику приложения и служит прослойкой между:
 * - Контроллером (принимает HTTP запросы)
 * - Репозиторием (работает с базой данных)
 * 
 * Реализует паттерн "Сервис" для инкапсуляции бизнес-правил.
 */
@Service // Помечает класс как Spring Bean сервисного слоя
public class ProductService {
    
    // Внедрение зависимости репозитория для работы с БД
    private final ProductRepository productRepository;

    /**
     * Конструктор с внедрением зависимости (Dependency Injection)
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * ПОЛУЧЕНИЕ ВСЕХ ПРОДУКТОВ С КЭШИРОВАНИЕМ
     * 
     * Результат кэшируется в кэше "products" с ключом имени метода.
     * При повторных вызовах данные берутся из кэша, а не из БД.
     * 
     * @return List<Product> - список всех продуктов
     */
    @Cacheable(value = "products", key = "#root.methodName")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    /**
     * ПОЛУЧЕНИЕ ПРОДУКТА ПО ID С КЭШИРОВАНИЕМ
     * 
     * Каждый продукт кэшируется отдельно с ключом = его ID.
     * Например: продукт с id=1 сохраняется как "product::1"
     * 
     * @param id - идентификатор продукта
     * @return Product - найденный продукт или null если не найден
     */
    @Cacheable(value = "product", key = "#id")
    public Product getById(Long id) {
        // orElse(null) - возвращает null если продукт не найден
        return productRepository.findById(id).orElse(null);
    }

    /**
     * СОЗДАНИЕ НОВОГО ПРОДУКТА
     * 
     * Сохраняет продукт в базу данных.
     * При этом кэш "products" становится неактуальным.
     * 
     * @param product - новый продукт для сохранения
     * @return Product - сохраненный продукт (с присвоенным ID)
     */
    public Product create(Product product) {
        return productRepository.save(product);
    }

    /**
     * ОБНОВЛЕНИЕ СУЩЕСТВУЮЩЕГО ПРОДУКТА
     * 
     * Находит продукт по ID и обновляет его данные.
     * Использует функциональный подход с map().
     * 
     * @param id - ID обновляемого продукта
     * @param updatedProduct - новые данные продукта
     * @return Product - обновленный продукт или null если не найден
     */
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

    /**
     * УДАЛЕНИЕ ПРОДУКТА ПО ID
     * 
     * Проверяет существование продукта перед удалением.
     * 
     * @param id - ID удаляемого продукта
     * @return boolean - true если удален, false если не найден
     */
    public boolean deleteById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * ПОИСК ПРОДУКТОВ ПО НАЗВАНИЮ (БЕЗ УЧЕТА РЕГИСТРА)
     * 
     * Ищет продукты, в названии которых содержится искомая строка.
     * 
     * @param title - искомая строка в названии
     * @return List<Product> - список найденных продуктов
     */
    public List<Product> getByTitle(String title) {
        if (title != null) {
            // Поиск с игнорированием регистра (ContainingIgnoreCase)
            return productRepository.findByTitleContainingIgnoreCase(title);
        }
        // Если title не указан - возвращаем все продукты
        return productRepository.findAll();
    } 
    
    /**
     * РАСШИРЕННАЯ ФИЛЬТРАЦИЯ С ПАГИНАЦИЕЙ
     * 
     * Использует спецификации для сложных запросов и пагинацию
     * для работы с большими объемами данных.
     * 
     * @param title - фильтр по названию
     * @param min - минимальная цена
     * @param max - максимальная цена  
     * @param pageable - параметры пагинации (страница, размер, сортировка)
     * @return Page<Product> - страница с продуктами и метаданными
     */
    public Page<Product> getByFilter(String title, Integer min, Integer max, Pageable pageable) {
        return productRepository.findAll(
            // Создаем спецификацию для фильтрации
            ProductSpecification.filter(title, min, max),
            pageable); // Применяем пагинацию
    }
}