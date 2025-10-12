package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import jakarta.validation.Valid;

/**
 * ОСНОВНОЙ КОНТРОЛЛЕР REST API ДЛЯ РАБОТЫ С ПРОДУКТАМИ
 * 
 * Этот класс обрабатывает все HTTP запросы, связанные с продуктами.
 * Предоставляет полный CRUD (Create, Read, Update, Delete) функционал.
 */
@RestController // Помечает класс как REST контроллер, возвращающий данные в формате JSON
public class MainController {
    
    // Внедрение зависимости сервиса продуктов через конструктор
    private final ProductService productService;

    /**
     * КОНСТРУКТОР С ВНЕДРЕНИЕМ ЗАВИСИМОСТИ
     * Spring автоматически передает реализацию ProductService
     */
    public MainController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * ПОЛУЧЕНИЕ ВСЕХ ПРОДУКТОВ ИЛИ ПОИСК ПО НАЗВАНИЮ
     * GET /products?title=... (параметр title необязательный)
     * 
     * @param title - необязательный параметр для поиска по названию
     * @return List<Product> - список продуктов (все или отфильтрованные)
     */
    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) String title) {
        if (title != null) {
            // Если передан параметр title - ищем продукты по названию
            return productService.getByTitle(title);
        }
        // Если параметр не передан - возвращаем все продукты
        return productService.getAll();
    }

    /**
     * ПОЛУЧЕНИЕ КОНКРЕТНОГО ПРОДУКТА ПО ID
     * GET /products/{id}
     * 
     * @param id - идентификатор продукта (из URL пути)
     * @return ResponseEntity<Product> - продукт с HTTP статусом:
     *         - 200 OK если продукт найден
     *         - 404 Not Found если продукт не найден
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            return ResponseEntity.ok(product); // 200 OK + данные продукта
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * СОЗДАНИЕ НОВОГО ПРОДУКТА
     * POST /products
     * 
     * @param product - данные продукта из тела запроса (в формате JSON)
     * @return ResponseEntity<Product> - созданный продукт со статусом 201 Created
     */
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid Product product) {
        // @Valid проверяет валидность данных (аннотации в классе Product)
        Product createdProduct = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct); // 201 Created
    }

    /**
     * ОБНОВЛЕНИЕ СУЩЕСТВУЮЩЕГО ПРОДУКТА
     * PUT /products/{id}
     * 
     * @param id - идентификатор обновляемого продукта
     * @param updatedProduct - новые данные продукта
     * @return ResponseEntity<Product> - обновленный продукт или 404 если не найден
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product updatedProduct) {
        Product product = productService.updateById(id, updatedProduct);
        if (product != null) {
            return ResponseEntity.ok(product); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * УДАЛЕНИЕ ПРОДУКТА
     * DELETE /products/{id}
     * 
     * @param id - идентификатор удаляемого продукта
     * @return ResponseEntity<Void> - 204 No Content если удалено, 404 если не найден
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteById(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * РАСШИРЕННАЯ ФИЛЬТРАЦИЯ ПРОДУКТОВ С ПАГИНАЦИЕЙ
     * GET /products/filter?title=...&min=...&max=...&page=...&size=...&sort=...
     * 
     * @param title - необязательный фильтр по названию
     * @param min   - необязательная минимальная цена
     * @param max   - необязательная максимальная цена
     * @param pageable - параметры пагинации (страница, размер, сортировка)
     * @return ResponseEntity<Object> - страница с продуктами и метаданными пагинации
     */
    @GetMapping("/products/filter")
    public ResponseEntity<Object> getProducts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @PageableDefault(page = 0, size = 10, sort = "title") // Значения по умолчанию
            Pageable pageable) {
        return ResponseEntity.ok(productService.getByFilter(title, min, max, pageable));
    }
}