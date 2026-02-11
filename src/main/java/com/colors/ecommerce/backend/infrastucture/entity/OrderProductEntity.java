package com.colors.ecommerce.backend.infrastucture.entity;

import jakarta.persistence.*;
import lombok.Data;



import java.math.BigDecimal;

@Data
@Entity
@Table(name="order_products")
public class OrderProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private BigDecimal quantity;
    private BigDecimal price;
    private Integer productVariantId;
    @Enumerated(EnumType.STRING)
    private com.colors.ecommerce.backend.domain.model.DeliveryType deliveryType;
    private java.time.LocalDate estimatedDeliveryDate;
    private String deliveryNote;
    @ManyToOne
    private OrderEntity orderEntity;
}
