package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.ProductService;
import com.colors.ecommerce.backend.application.HeroCarouselSlideService;
import com.colors.ecommerce.backend.domain.model.Product;
import com.colors.ecommerce.backend.infrastucture.rest.dto.HeroCarouselSlideDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://lionsbrand.com.ar", "https://www.lionsbrand.com.ar", "https://ecommerce-angular-production.up.railway.app"})
public class HomeController {
    private final ProductService productService;
    private final HeroCarouselSlideService heroCarouselSlideService;

    public HomeController(ProductService productService, HeroCarouselSlideService heroCarouselSlideService) {
        this.productService = productService;
        this.heroCarouselSlideService = heroCarouselSlideService;
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

    @GetMapping("/hero-slides")
    public ResponseEntity<List<HeroCarouselSlideDto>> getHeroSlides() {
        return new ResponseEntity<>(heroCarouselSlideService.findAll(), HttpStatus.OK);
    }
}
