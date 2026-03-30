package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.infrastucture.adapter.IHeroCarouselSlideCrudRepository;
import com.colors.ecommerce.backend.infrastucture.entity.HeroCarouselSlideEntity;
import com.colors.ecommerce.backend.infrastucture.rest.dto.HeroCarouselSlideDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeroCarouselSlideService {
    private final IHeroCarouselSlideCrudRepository heroCarouselSlideCrudRepository;

    public HeroCarouselSlideService(IHeroCarouselSlideCrudRepository heroCarouselSlideCrudRepository) {
        this.heroCarouselSlideCrudRepository = heroCarouselSlideCrudRepository;
    }

    public List<HeroCarouselSlideDto> findAll() {
        List<HeroCarouselSlideEntity> entities = heroCarouselSlideCrudRepository.findAllByOrderByDisplayOrderAsc();
        if (entities.isEmpty()) {
            return defaultSlides();
        }
        return entities.stream().map(this::toDto).toList();
    }

    @Transactional
    public List<HeroCarouselSlideDto> replaceAll(List<HeroCarouselSlideDto> slides) {
        if (slides == null || slides.isEmpty()) {
            throw new RuntimeException("At least one hero slide is required");
        }

        List<HeroCarouselSlideEntity> entities = new ArrayList<>();
        for (int i = 0; i < slides.size(); i++) {
            HeroCarouselSlideDto slide = slides.get(i);
            if (slide.title() == null || slide.title().isBlank() || slide.ctaText() == null || slide.ctaText().isBlank() || slide.image() == null || slide.image().isBlank()) {
                throw new RuntimeException("Each slide needs title, ctaText and image");
            }
            HeroCarouselSlideEntity entity = new HeroCarouselSlideEntity();
            entity.setDisplayOrder(i);
            entity.setEyebrow(normalize(slide.eyebrow()));
            entity.setTitle(slide.title().trim());
            entity.setSubtitle(normalize(slide.subtitle()));
            entity.setCtaText(slide.ctaText().trim());
            entity.setCtaLink(slide.ctaLink() == null || slide.ctaLink().isBlank() ? "/product" : slide.ctaLink().trim());
            entity.setImage(slide.image().trim());
            entity.setAlign("center".equalsIgnoreCase(slide.align()) ? "center" : "left");
            entities.add(entity);
        }

        heroCarouselSlideCrudRepository.deleteAll();
        heroCarouselSlideCrudRepository.saveAll(entities);
        return findAll();
    }

    public List<HeroCarouselSlideDto> reset() {
        return replaceAll(defaultSlides());
    }

    private HeroCarouselSlideDto toDto(HeroCarouselSlideEntity entity) {
        return new HeroCarouselSlideDto(
                entity.getId(),
                entity.getEyebrow(),
                entity.getTitle(),
                entity.getSubtitle(),
                entity.getCtaText(),
                entity.getCtaLink(),
                entity.getImage(),
                entity.getAlign()
        );
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    private List<HeroCarouselSlideDto> defaultSlides() {
        return List.of(
                new HeroCarouselSlideDto(null, "Lions Brand BJJ", "Equipate para dominar el tatami",
                        "Drops visuales, siluetas limpias y una tienda pensada para entrar, mirar y comprar rapido.",
                        "Comprar ahora", "/product", "assets/bjj/kimono1.jpg", "left"),
                new HeroCarouselSlideDto(null, "Rashguards + Fightwear", "Disenos que pegan primero",
                        "Compresion, color y lectura inmediata del catalogo en una home mas editorial y directa.",
                        "Ver coleccion", "/product", "assets/bjj/rashguard1.png", "center"),
                new HeroCarouselSlideDto(null, "Coleccion Lions", "Menos ruido. Mas presencia.",
                        "Tipografia fuerte, imagenes completas y navegacion simple para que el producto haga el trabajo.",
                        "Explorar productos", "/product", "assets/bjj/kimono3.jpg", "left")
        );
    }
}
