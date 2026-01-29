package com.colors.ecommerce.backend.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockMovement {
    private Integer id;
    private Integer variantId;
    private StockMovementType type;
    private Integer qty;
    private StockMovementReason reason;
    private Integer orderId;
    private String note;
    private LocalDateTime createdAt;
    private String createdBy;
}
