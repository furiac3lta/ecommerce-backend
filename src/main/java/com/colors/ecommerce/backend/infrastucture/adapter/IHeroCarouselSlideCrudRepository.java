package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.HeroCarouselSlideEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IHeroCarouselSlideCrudRepository extends CrudRepository<HeroCarouselSlideEntity, Integer> {
    List<HeroCarouselSlideEntity> findAllByOrderByDisplayOrderAsc();
}
