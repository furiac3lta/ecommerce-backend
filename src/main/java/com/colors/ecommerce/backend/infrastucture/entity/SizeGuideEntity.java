package com.colors.ecommerce.backend.infrastucture.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "size_guides")
@Data
@NoArgsConstructor
public class SizeGuideEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String size;
    private Integer heightRecommendedCm;
    private Integer weightRecommendedKg;
    private String description;
}
