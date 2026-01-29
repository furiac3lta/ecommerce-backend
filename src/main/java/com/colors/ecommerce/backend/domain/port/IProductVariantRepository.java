package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.ProductVariant;

public interface IProductVariantRepository {
    ProductVariant save(ProductVariant productVariant);
    Iterable<ProductVariant> findAll();
    Iterable<ProductVariant> findByProductId(Integer productId);
    ProductVariant findById(Integer id);
    ProductVariant findByIdForUpdate(Integer id);
    void deleteById(Integer id);
}
