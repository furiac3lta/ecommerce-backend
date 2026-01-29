package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.ProductVariantService;
import com.colors.ecommerce.backend.domain.model.ProductVariant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @PostMapping("/api/v1/admin/variants")
    public ResponseEntity<ProductVariant> save(@RequestBody ProductVariant productVariant) {
        log.info("Saving product variant for product {}", productVariant.getProductId());
        return new ResponseEntity<>(productVariantService.save(productVariant), HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/variants")
    public ResponseEntity<Iterable<ProductVariant>> findAll() {
        return ResponseEntity.ok(productVariantService.findAll());
    }

    @GetMapping("/api/v1/variants/{id}")
    public ResponseEntity<ProductVariant> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productVariantService.findById(id));
    }

    @GetMapping("/api/v1/variants/by-product/{productId}")
    public ResponseEntity<Iterable<ProductVariant>> findByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(productVariantService.findByProductId(productId));
    }

    @DeleteMapping("/api/v1/admin/variants/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productVariantService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
