package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.CloudinaryUploadFile;
import com.colors.ecommerce.backend.application.HeroCarouselSlideService;
import com.colors.ecommerce.backend.infrastucture.rest.dto.HeroCarouselSlideDto;
import com.colors.ecommerce.backend.infrastucture.rest.dto.UploadedImageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tools")
public class AdminToolsController {
    private final HeroCarouselSlideService heroCarouselSlideService;
    private final CloudinaryUploadFile cloudinaryUploadFile;

    public AdminToolsController(HeroCarouselSlideService heroCarouselSlideService, CloudinaryUploadFile cloudinaryUploadFile) {
        this.heroCarouselSlideService = heroCarouselSlideService;
        this.cloudinaryUploadFile = cloudinaryUploadFile;
    }

    @PostMapping("/hero-slides")
    public ResponseEntity<List<HeroCarouselSlideDto>> saveHeroSlides(@RequestBody List<HeroCarouselSlideDto> slides) {
        return new ResponseEntity<>(heroCarouselSlideService.replaceAll(slides), HttpStatus.OK);
    }

    @PostMapping("/hero-slides/reset")
    public ResponseEntity<List<HeroCarouselSlideDto>> resetHeroSlides() {
        return new ResponseEntity<>(heroCarouselSlideService.reset(), HttpStatus.OK);
    }

    @PostMapping("/hero-slides/image")
    public ResponseEntity<UploadedImageDto> uploadHeroSlideImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }
        String imageUrl = cloudinaryUploadFile.upload(file);
        return new ResponseEntity<>(new UploadedImageDto(file.getOriginalFilename(), imageUrl), HttpStatus.OK);
    }
}
