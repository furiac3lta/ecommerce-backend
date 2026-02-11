package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IProductCrudRepository  extends CrudRepository<ProductEntity, Integer> {
    Optional<ProductEntity> findByNameIgnoreCaseAndBrand(String name, String brand);
    Optional<ProductEntity> findByNameIgnoreCase(String name);
    Iterable<ProductEntity> findByCategoryEntityId(Integer categoryId);
}
