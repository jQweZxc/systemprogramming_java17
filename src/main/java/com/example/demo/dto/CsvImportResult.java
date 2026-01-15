package com.example.demo.dto;

import java.util.List;

public record CsvImportResult(
    int successCount,
    int failedCount,
    List<String> errors
) {
    public boolean hasError() {
        return failedCount > 0 || (errors != null && !errors.isEmpty());
    }
}