package com.example.demo.controller;

import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.SensorService;
import com.example.demo.service.FileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final SensorService sensorService;

    @PreAuthorize("hasAuthority('FILE_UPLOAD')")
    @PostMapping(value = "/upload/{id}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String resultFile = fileService.storeFile(file);
            sensorService.addFileToSensorData(id, resultFile);
                return ResponseEntity.ok(resultFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}