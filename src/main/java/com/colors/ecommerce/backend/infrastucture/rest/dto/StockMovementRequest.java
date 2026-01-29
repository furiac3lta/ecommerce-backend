package com.colors.ecommerce.backend.infrastucture.rest.dto;

import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import lombok.Data;

@Data
public class StockMovementRequest {
    private Integer variantId;
    private Integer qty;
    private StockMovementType type;
    private StockMovementReason reason;
    private Integer orderId;
    private String note;
    private String createdBy;
}
