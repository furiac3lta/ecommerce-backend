package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Category;
import com.colors.ecommerce.backend.domain.model.Product;
import com.colors.ecommerce.backend.domain.port.ICategoryRepository;
import com.colors.ecommerce.backend.domain.port.IProductRepository;

public class CategoryService {
    private final ICategoryRepository iCategoryRepository;
    private final IProductRepository productRepository;

    public CategoryService(ICategoryRepository iCategoryRepository, IProductRepository productRepository) {
        this.iCategoryRepository = iCategoryRepository;
        this.productRepository = productRepository;
    }

    public Category save(Category category) {
        Category previous = null;
        if (category.getId() != null) {
            try {
                previous = iCategoryRepository.findById(category.getId());
            } catch (Exception ignored) {
            }
        }
        Category saved = iCategoryRepository.save(category);
        if (saved.getPrice() != null) {
            boolean priceChanged = previous == null || previous.getPrice() == null
                    || saved.getPrice().compareTo(previous.getPrice()) != 0;
            if (priceChanged) {
                Iterable<Product> products = productRepository.findByCategoryId(saved.getId());
                for (Product product : products) {
                    if (Boolean.TRUE.equals(product.getPriceOverride())) {
                        continue;
                    }
                    product.setPrice(saved.getPrice());
                    productRepository.save(product);
                }
            }
        }
        return saved;
    }

    public Iterable<Category> findAll() {
        return iCategoryRepository.findAll();
    }

    public Category findById(Integer id) {
        return iCategoryRepository.findById(id);
    }

    public void delete(Integer id) {
        iCategoryRepository.deleteById(id);
    }
}
