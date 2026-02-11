package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

@Data
public class ReturnItemRequest {
    private Integer variantId;
    private Integer qty;
    private String reason;
    private String note;
}
