package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.Product;

public interface IProductRepository {
    Product save(Product product);
    Iterable<Product> findAll();
    Product findById(Integer id);
    Iterable<Product> findByCategoryId(Integer categoryId);
    void deleteById(Integer id);
}
