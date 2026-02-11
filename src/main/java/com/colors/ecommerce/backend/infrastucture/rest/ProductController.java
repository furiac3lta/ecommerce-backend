package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.ProductService;
import com.colors.ecommerce.backend.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/api/v1/admin/products")
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> save(@RequestParam("id") Integer id,
                                        @RequestParam("code") String code,
                                        @RequestParam("name") String name,
                                        @RequestParam("description") String description,
                                        @RequestParam("price") BigDecimal price,
                                        @RequestParam(value = "priceOverride", required = false) Boolean priceOverride,
                                        @RequestParam(value = "sellOnline", required = false) Boolean sellOnline,
                                        @RequestParam(value = "deliveryType", required = false) com.colors.ecommerce.backend.domain.model.DeliveryType deliveryType,
                                        @RequestParam(value = "estimatedDeliveryDays", required = false) Integer estimatedDeliveryDays,
                                        @RequestParam(value = "estimatedDeliveryDate", required = false)
                                        @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate estimatedDeliveryDate,
                                        @RequestParam(value = "deliveryNote", required = false) String deliveryNote,
                                        @RequestParam("urlImage") String urlImage,
                                        @RequestParam("userId") Integer userId,
                                        @RequestParam("categoryId") Integer categoryId,
                                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                        @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        Product product = new Product();
        product.setId(id);
        product.setCode(code);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setPriceOverride(priceOverride != null ? priceOverride : false);
        product.setSellOnline(sellOnline != null ? sellOnline : true);
        product.setDeliveryType(deliveryType);
        product.setEstimatedDeliveryDays(estimatedDeliveryDays);
        product.setEstimatedDeliveryDate(estimatedDeliveryDate);
        product.setDeliveryNote(deliveryNote);
        product.setUrlImage(urlImage);
        product.setUserId(userId);
        product.setCategoryId(categoryId);

        log.info("Saving product {}", product.getName());
        return new ResponseEntity<>(productService.save(product, images, multipartFile), HttpStatus.CREATED);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteById(@PathVariable("id") Integer id) throws IOException {
        log.info("Deleting product by id {}", id);
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
