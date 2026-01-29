package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.CategoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ICategoryCrudRepository extends CrudRepository<CategoryEntity, Integer> {
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
}
