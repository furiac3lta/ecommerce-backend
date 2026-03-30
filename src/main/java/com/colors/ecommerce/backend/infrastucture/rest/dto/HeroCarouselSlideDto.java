package com.colors.ecommerce.backend.infrastucture.rest.dto;

public record HeroCarouselSlideDto(
        Integer id,
        String eyebrow,
        String title,
        String subtitle,
        String ctaText,
        String ctaLink,
        String image,
        String align
) {
}
