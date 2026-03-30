package com.colors.ecommerce.backend.infrastucture.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hero_carousel_slides")
@Data
@NoArgsConstructor
public class HeroCarouselSlideEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer displayOrder;
    private String carouselKey;
    private String eyebrow;
    private String title;
    private String subtitle;
    private String ctaText;
    private String ctaLink;
    private String image;
    private String align;
}
