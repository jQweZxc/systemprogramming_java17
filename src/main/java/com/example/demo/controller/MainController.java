package com.example.demo.controller;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;

@RestController
public class MainController {
    
    private final ProductService productService;
    
    public MainController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) String title) {
        if (title != null) {
            return productService.getByTitle(title);
        }
        return productService.getAll();
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid Product product) {
        Product createdProduct = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product updatedProduct) {
        Product product = productService.updateById(id, updatedProduct);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @GetMapping("/products/filter")
    public ResponseEntity<Object> getProducts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @PageableDefault(page = 0, size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(productService.getByFilter(title, min, max, pageable));
    }
}