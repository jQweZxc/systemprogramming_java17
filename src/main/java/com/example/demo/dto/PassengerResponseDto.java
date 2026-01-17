package com.example.demo.dto;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerResponseDto {
    private Long id;
    private LocalDateTime timestamp;
    private Long busId;
    private String busModel;
    private Long stopId;
    private String stopName;
    private Integer entered;
    private Integer exited;{
        this.id = id;
        this.timestamp = timestamp;
        this.busId = busId;
        this.busModel = busModel;
        this.stopId = stopId;
        this.stopName = stopName;
        this.entered = entered;
        this.exited = exited;
    }
}