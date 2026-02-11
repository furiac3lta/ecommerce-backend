package com.colors.ecommerce.backend.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProduct {
    private Integer id;
    private BigDecimal quantity;
    private BigDecimal price;
    private Integer productVariantId;
    private DeliveryType deliveryType;
    private java.time.LocalDate estimatedDeliveryDate;
    private String deliveryNote;

    public BigDecimal getTotalItem() {
        return this.price.multiply(this.quantity);
    }
}
