package com.example.demo.model;

import java.time.LocalDateTime;

//данные о пассажире, который зашел на остановке
public class PassengerCount {
    private Long id;
    private Bus bus;
    private Stop stop;
    private Integer entered;
    private Integer exited;
    private LocalDateTime timestampe;
}
