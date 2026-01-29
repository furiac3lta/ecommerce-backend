package com.colors.ecommerce.backend.domain.model;

import lombok.Data;

@Data
public class SizeGuide {
    private Integer id;
    private String size;
    private Integer heightRecommendedCm;
    private Integer weightRecommendedKg;
    private String description;
}
