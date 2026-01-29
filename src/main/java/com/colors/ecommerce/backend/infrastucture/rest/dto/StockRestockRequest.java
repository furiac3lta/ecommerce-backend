package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

@Data
public class StockRestockRequest {
    private Integer variantId;
    private Integer qty;
    private String note;
    private String createdBy;
}
