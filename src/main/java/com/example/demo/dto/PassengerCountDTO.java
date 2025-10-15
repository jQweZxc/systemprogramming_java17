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

record BusDTO(
    Long id,
    String model,
    RouteDTO route
) {}

record StopDTO(
    Long id,
    String name,
    Double lat,
    Double lon
) {}

record RouteDTO(
    Long id
) {}