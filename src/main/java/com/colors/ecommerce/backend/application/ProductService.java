package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Category;
import com.colors.ecommerce.backend.domain.model.Product;
import com.colors.ecommerce.backend.domain.port.IProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductService {
    private final IProductRepository iProductRepository;
    private final CategoryService categoryService;
    private final CloudinaryUploadFile cloudinaryUploadFile;

    public ProductService(IProductRepository iProductRepository,
                          CloudinaryUploadFile cloudinaryUploadFile,
                          CategoryService categoryService) {
        this.iProductRepository = iProductRepository;
        this.cloudinaryUploadFile = cloudinaryUploadFile;
        this.categoryService = categoryService;
    }

    public Product save(Product product, List<MultipartFile> images, MultipartFile multipartFile) throws IOException {
        if (product.getBrand() == null || product.getBrand().isBlank()) {
            product.setBrand("LION'S BRAND");
        }
        if (product.getActive() == null) {
            product.setActive(true);
        }
        if (product.getSellOnline() == null) {
            product.setSellOnline(true);
        }
        if (product.getDeliveryType() == null) {
            product.setDeliveryType(com.colors.ecommerce.backend.domain.model.DeliveryType.IMMEDIATE);
        }
        if (product.getSlug() == null || product.getSlug().isBlank()) {
            product.setSlug(toSlug(product.getName()));
        }

        if (product.getCategoryId() == null) {
            throw new RuntimeException("Category is required to set price");
        }
        boolean overridePrice = Boolean.TRUE.equals(product.getPriceOverride());
        if (!overridePrice) {
            Category category = categoryService.findById(product.getCategoryId());
            if (category == null) {
                throw new RuntimeException("Category not found: " + product.getCategoryId());
            }
            if (category.getPrice() == null) {
                throw new RuntimeException("Category price is not set for category " + product.getCategoryId());
            }
            product.setPrice(category.getPrice());
        } else if (product.getPrice() == null) {
            throw new RuntimeException("Custom price is required when priceOverride is true");
        }

        Product existing = null;
        if (product.getId() != null) {
            existing = iProductRepository.findById(product.getId());
        }

        List<String> uploadedImages = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                String uploadResult = cloudinaryUploadFile.upload(file);
                if (uploadResult != null) {
                    uploadedImages.add(uploadResult);
                }
            }
        }

        if (uploadedImages.isEmpty() && multipartFile != null && !multipartFile.isEmpty()) {
            String uploadResult = cloudinaryUploadFile.upload(multipartFile);
            if (uploadResult != null) {
                uploadedImages.add(uploadResult);
            }
        }

        if (!uploadedImages.isEmpty()) {
            List<String> finalImages = new ArrayList<>();
            if (existing != null && existing.getImages() != null && !existing.getImages().isEmpty()) {
                finalImages.addAll(existing.getImages());
            }
            finalImages.addAll(uploadedImages);
            product.setImages(finalImages);
            if (product.getUrlImage() == null || product.getUrlImage().isBlank()) {
                product.setUrlImage(finalImages.get(0));
            }
            product.setPublicId(getPublicIdFromUrl(product.getUrlImage()));
            log.info("Imagenes guardadas para producto: {}", finalImages.size());
        } else {
            if (product.getImages() == null && existing != null && existing.getImages() != null) {
                product.setImages(existing.getImages());
            }
            if ((product.getUrlImage() == null || product.getUrlImage().isEmpty()) && existing != null) {
                product.setUrlImage(existing.getUrlImage());
                product.setPublicId(existing.getPublicId());
            }
            if (product.getUrlImage() == null || product.getUrlImage().isEmpty()) {
                product.setUrlImage("https://ecommerce-back-0cc9b90e39e5.herokuapp.com/images/default.jpg");
                product.setPublicId("default");
                log.info("Usando imagen por defecto para el producto: {}", product.getName());
            }
        }

        return iProductRepository.save(product);
    }

    public Iterable<Product> findAll() {
        Iterable<Product> products = iProductRepository.findAll();
        for (Product product : products) {
            applyPricing(product);
        }
        return products;
    }

    public Iterable<Product> findAllOnline() {
        Iterable<Product> products = findAll();
        java.util.List<Product> filtered = new java.util.ArrayList<>();
        for (Product product : products) {
            if (product == null) {
                continue;
            }
            if (product.getSellOnline() == null || Boolean.TRUE.equals(product.getSellOnline())) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    public Product findById(Integer id) {
        Product product = iProductRepository.findById(id);
        applyPricing(product);
        return product;
    }

    public void delete(Integer id) throws IOException {
        Product product = findById(id);
        if (product != null && product.getPublicId() != null && !product.getPublicId().equals("default")) {
            cloudinaryUploadFile.delete(product.getPublicId());
            log.info("Imagen eliminada: {}", product.getPublicId());
        }
        this.iProductRepository.deleteById(id);
    }


    private void applyPricing(Product product) {
        if (product == null) {
            return;
        }
        if (Boolean.TRUE.equals(product.getPriceOverride())) {
            return;
        }
        if (product.getCategoryId() == null) {
            return;
        }
        try {
            Category category = categoryService.findById(product.getCategoryId());
            if (category != null && category.getPrice() != null) {
                product.setPrice(category.getPrice());
            }
        } catch (Exception ex) {
            log.warn("No se pudo aplicar precio de categoría para producto {}", product.getId());
        }
    }

    private String getPublicIdFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
    }

    private String toSlug(String value) {
        if (value == null) {
            return null;
        }
        return value.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
