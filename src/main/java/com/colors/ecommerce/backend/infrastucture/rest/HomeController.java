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
        return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") Integer id) {
        log.info("Finding product by id {}", id);
        return new ResponseEntity<>(productService.findById(id), HttpStatus.OK);
    }
}
