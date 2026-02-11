package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeItemRequest {
    private Integer variantId;
    private Integer qty;
    private BigDecimal unitPrice;
    private String note;
}
