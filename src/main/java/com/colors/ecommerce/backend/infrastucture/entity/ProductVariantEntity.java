package com.colors.ecommerce.backend.infrastucture.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
public class ProductVariantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private ProductEntity productEntity;

    private String size;
    private String color;
    private BigDecimal gsm;
    private String material;
    private String usage;
    @Column(unique = true)
    private String sku;
    private BigDecimal priceRetail;
    private BigDecimal priceWholesale;
    @Enumerated(EnumType.STRING)
    private com.colors.ecommerce.backend.domain.model.DeliveryType deliveryType;
    private Integer estimatedDeliveryDays;
    private java.time.LocalDate estimatedDeliveryDate;
    private String deliveryNote;
    private Integer stockCurrent;
    private Integer stockMinimum;
    private Boolean active;
    private Boolean sellOnline;
}
