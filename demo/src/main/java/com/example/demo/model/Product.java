package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="products")

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Название объекта")
    @Column(nullable = false, unique=true, length=100)
    @NotBlank(message = "Название пустое")
    @Size(min=2, max=100, message="Название должно быть от 2 до 100 символов")
    private String title;
    @Column(nullable = false)
    @Min(1)
    private int price;
}
