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
    @ManyToOne
    private OrderEntity orderEntity;
}
