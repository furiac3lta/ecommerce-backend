package com.colors.ecommerce.backend.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariant {
    private Integer id;
    private Integer productId;
    private String size;
    private String color;
    private BigDecimal gsm;
    private String material;
    private String usage;
    private String sku;
    private Integer stockCurrent;
    private Integer stockMinimum;
    private Boolean active;
    private Integer reservedStock;
    private Integer availableStock;
    private String productName;
}
