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
    private BigDecimal priceRetail;
    private BigDecimal priceWholesale;
    private DeliveryType deliveryType;
    private Integer estimatedDeliveryDays;
    private java.time.LocalDate estimatedDeliveryDate;
    private String deliveryNote;
    private Integer stockCurrent;
    private Integer stockMinimum;
    private Boolean active;
    private Boolean sellOnline;
    private Integer reservedStock;
    private Integer availableStock;
    private String productName;
}
