package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.ProductService;
import com.colors.ecommerce.backend.domain.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class HomeController {
    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    public ResponseEntity<Iterable<Product>> findAll() {
        log.info("Finding all products");
        return new ResponseEntity<>(productService.findAllOnline(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") Integer id) {
        log.info("Finding product by id {}", id);
        Product product = productService.findById(id);
        if (product.getSellOnline() != null && !product.getSellOnline()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}
