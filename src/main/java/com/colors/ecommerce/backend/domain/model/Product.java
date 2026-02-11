package com.colors.ecommerce.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private String slug;
    private String brand;
    private String code;
    private String description;
    private String urlImage;
    private String publicId;
    private List<String> images;
    private BigDecimal price;
    private Boolean priceOverride;
    private Boolean sellOnline;
    private DeliveryType deliveryType;
    private Integer estimatedDeliveryDays;
    private java.time.LocalDate estimatedDeliveryDate;
    private String deliveryNote;
    private Boolean active;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private Integer userId;
    private Integer categoryId;
}
