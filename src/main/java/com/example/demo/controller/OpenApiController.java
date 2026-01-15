package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/openapi")
@CrossOrigin("*")
public class OpenApiController {
    
    @GetMapping("/hello")
    public String hello() {
        return "{\"status\": \"OK\", \"message\": \"Spring Boot is running!\", \"timestamp\": \"" 
                + new java.util.Date() + "\"}";
    }
    
    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse(
            "UP",
            "Spring Boot 3.5.9",
            System.getProperty("java.version"),
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB"
        );
    }
    
    @GetMapping("/test/{name}")
    public String test(@PathVariable String name) {
        return "{\"greeting\": \"Hello " + name + "!\", \"app\": \"Spring Boot Demo\"}";
    }
    
    record HealthResponse(
        String status, 
        String framework,
        String javaVersion,
        int availableProcessors,
        String maxMemory
    ) {}
}