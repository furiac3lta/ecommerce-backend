package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.SizeGuideService;
import com.colors.ecommerce.backend.domain.model.SizeGuide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar", "https://ecommerce-angular-production.up.railway.app"})
public class SizeGuideController {
    private final SizeGuideService sizeGuideService;

    public SizeGuideController(SizeGuideService sizeGuideService) {
        this.sizeGuideService = sizeGuideService;
    }

    @PostMapping("/api/v1/admin/size-guides")
    public ResponseEntity<SizeGuide> save(@RequestBody SizeGuide sizeGuide) {
        return new ResponseEntity<>(sizeGuideService.save(sizeGuide), HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/size-guides")
    public ResponseEntity<Iterable<SizeGuide>> findAll() {
        return ResponseEntity.ok(sizeGuideService.findAll());
    }

    @DeleteMapping("/api/v1/admin/size-guides/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sizeGuideService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
