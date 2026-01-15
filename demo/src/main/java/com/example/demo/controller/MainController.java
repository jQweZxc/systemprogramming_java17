package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController

public class MainController {
    private ProductService productService;
    
    public MainController(ProductService productService) {
        this.productService = productService;
    }

    private List<Product> products=new ArrayList<>(Arrays.asList(new Product(1l, "Name", 100)));
    @GetMapping("/products")
    public List<Product> getMethodName() {
        return productService.getAll();
    }

    @PostMapping("/products/{id}")
    public ResponseEntity<Product> postMethodName(@RequestBody @Valid Product product) {    
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(product));

//        (@PathValidLong id){
//            for (Product product : pro Ser getAll(i)) {                
//            }
//        }
    }

    
}
