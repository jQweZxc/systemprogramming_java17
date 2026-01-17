//данные о пассажире, который зашел на остановке
package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "passenger_counts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerCount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)

    @NotNull(message = "Автобус не может быть пустым")
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false)

    @NotNull(message = "Остановка не может быть пустой")
    private Stop stop;

    @Column(nullable = false)
    @Min(value = 0, message = "Количество вошедших не может быть отрицательным")
    private Integer entered;

    @Column(nullable = false)
    @Min(value = 0, message = "Количество вышедших не может быть отрицательным")
    private Integer exited;

    @Column(nullable = false)
    @NotNull(message = "Временная метка не может быть пустой")
    private LocalDateTime timestamp;
}
