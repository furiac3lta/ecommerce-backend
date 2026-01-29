package com.colors.ecommerce.backend.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockReservation {
    private Integer id;
    private Integer variantId;
    private Integer orderId;
    private Integer qty;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime releasedAt;
    private StockReservationStatus status;
}
