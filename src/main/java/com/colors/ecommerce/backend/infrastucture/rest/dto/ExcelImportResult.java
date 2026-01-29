package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelImportResult {
    private int created = 0;
    private int updated = 0;
    private List<ImportError> errors = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private int line;
        private String message;
    }
}
