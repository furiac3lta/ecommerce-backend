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
    public static final String HOME_HERO_KEY = "home-hero";
    public static final String HOME_EDITORIAL_KEY = "home-editorial";

    private final IHeroCarouselSlideCrudRepository heroCarouselSlideCrudRepository;

    public HeroCarouselSlideService(IHeroCarouselSlideCrudRepository heroCarouselSlideCrudRepository) {
        this.heroCarouselSlideCrudRepository = heroCarouselSlideCrudRepository;
    }

    public List<HeroCarouselSlideDto> findAll() {
        return findAllByKey(HOME_HERO_KEY);
    }

    public List<HeroCarouselSlideDto> findAllByKey(String carouselKey) {
        List<HeroCarouselSlideEntity> entities = loadEntitiesByKey(carouselKey);
        if (entities.isEmpty()) {
            return defaultSlides(normalizeKey(carouselKey));
        }
        return entities.stream().map(this::toDto).toList();
    }

    @Transactional
    public List<HeroCarouselSlideDto> replaceAll(List<HeroCarouselSlideDto> slides) {
        return replaceAll(HOME_HERO_KEY, slides);
    }

    @Transactional
    public List<HeroCarouselSlideDto> replaceAll(String carouselKey, List<HeroCarouselSlideDto> slides) {
        String normalizedKey = normalizeKey(carouselKey);
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
            entity.setCarouselKey(normalizedKey);
            entity.setEyebrow(normalize(slide.eyebrow()));
            entity.setTitle(slide.title().trim());
            entity.setSubtitle(normalize(slide.subtitle()));
            entity.setCtaText(slide.ctaText().trim());
            entity.setCtaLink(slide.ctaLink() == null || slide.ctaLink().isBlank() ? "/product" : slide.ctaLink().trim());
            entity.setImage(slide.image().trim());
            entity.setAlign("center".equalsIgnoreCase(slide.align()) ? "center" : "left");
            entities.add(entity);
        }

        List<HeroCarouselSlideEntity> existing = loadEntitiesByKey(normalizedKey);
        if (!existing.isEmpty()) {
            heroCarouselSlideCrudRepository.deleteAll(existing);
        }
        heroCarouselSlideCrudRepository.saveAll(entities);
        return findAllByKey(normalizedKey);
    }

    public List<HeroCarouselSlideDto> reset() {
        return reset(HOME_HERO_KEY);
    }

    public List<HeroCarouselSlideDto> reset(String carouselKey) {
        String normalizedKey = normalizeKey(carouselKey);
        return replaceAll(normalizedKey, defaultSlides(normalizedKey));
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

    private List<HeroCarouselSlideEntity> loadEntitiesByKey(String carouselKey) {
        String normalizedKey = normalizeKey(carouselKey);
        List<HeroCarouselSlideEntity> entities = heroCarouselSlideCrudRepository.findAllByCarouselKeyOrderByDisplayOrderAsc(normalizedKey);
        if (entities.isEmpty() && HOME_HERO_KEY.equals(normalizedKey)) {
            return heroCarouselSlideCrudRepository.findAllByCarouselKeyIsNullOrderByDisplayOrderAsc();
        }
        return entities;
    }

    private String normalizeKey(String carouselKey) {
        if (carouselKey == null || carouselKey.isBlank()) {
            return HOME_HERO_KEY;
        }
        return carouselKey.trim().toLowerCase();
    }

    private List<HeroCarouselSlideDto> defaultSlides(String carouselKey) {
        if (HOME_EDITORIAL_KEY.equals(carouselKey)) {
            return List.of(
                    new HeroCarouselSlideDto(null, "Lions Brand BJJ", "Diseno fuerte. Navegacion limpia. Compra directa.",
                            "Un segundo carrusel para reforzar la marca, sostener el tono editorial y empujar al catalogo desde la home.",
                            "Ver tienda", "/product", "assets/bjj/kimono2.jpg", "left"),
                    new HeroCarouselSlideDto(null, "Fightwear editorial", "Colecciones claras. Lectura rapida. Producto al frente.",
                            "Cada slide puede rotar mensajes, imagenes y CTA sin tocar codigo desde el admin.",
                            "Explorar", "/product", "assets/bjj/rashguard1.png", "left")
            );
        }

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
