package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimelineEventDto {
    private String type;
    private String label;
    private LocalDateTime timestamp;
    private String actor;
}
