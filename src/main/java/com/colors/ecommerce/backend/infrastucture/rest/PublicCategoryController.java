package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.CategoryService;
import com.colors.ecommerce.backend.domain.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar", "https://ecommerce-angular-production.up.railway.app"})
public class PublicCategoryController {

    private final CategoryService categoryService;

    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Category>> findAll() {
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(categoryService.findById(id), HttpStatus.OK);
    }
}
