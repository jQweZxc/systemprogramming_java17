package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Название остановки не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Широта не может быть пустой")
    private Double lat;

    @Column(nullable = false)
    @NotNull(message = "Долгота не может быть пустой")
    private Double lon;
}