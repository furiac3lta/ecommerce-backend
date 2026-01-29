package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

@Data
public class StockAdjustRequest {
    private Integer variantId;
    private Integer newStock;
    private String note;
    private String createdBy;
}
