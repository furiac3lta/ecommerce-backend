package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Category;
import com.colors.ecommerce.backend.domain.port.ICategoryRepository;
import com.colors.ecommerce.backend.domain.port.IProductRepository;

public class CategoryService {
    private final ICategoryRepository iCategoryRepository;


    public CategoryService(ICategoryRepository iCategoryRepository) {
        this.iCategoryRepository = iCategoryRepository;
    }

    public Category save(Category category) {
        return iCategoryRepository.save(category);
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
