package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.ProductVariantEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IProductVariantCrudRepository extends CrudRepository<ProductVariantEntity, Integer> {
    List<ProductVariantEntity> findByProductEntityId(Integer productId);
    ProductVariantEntity findBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT v FROM ProductVariantEntity v WHERE v.id = :id")
    ProductVariantEntity findByIdForUpdate(Integer id);
}
