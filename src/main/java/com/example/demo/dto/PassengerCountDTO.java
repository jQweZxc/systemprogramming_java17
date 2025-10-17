package com.example.demo.dto;

import java.time.LocalDateTime;

public record PassengerCountDTO(
    Long id,
    BusDTO bus,
    StopDTO stop,
    Integer entered,
    Integer exited,
    LocalDateTime timestamp
) {}