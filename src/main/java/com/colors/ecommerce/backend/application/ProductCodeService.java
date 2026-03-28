package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.infrastucture.adapter.IProductCrudRepository;
import com.colors.ecommerce.backend.infrastucture.entity.ProductEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductCodeService {
    private final IProductCrudRepository productCrudRepository;

    public ProductCodeService(IProductCrudRepository productCrudRepository) {
        this.productCrudRepository = productCrudRepository;
    }

    public String nextCode() {
        int maxCode = 0;
        for (ProductEntity product : productCrudRepository.findAll()) {
            if (product == null) {
                continue;
            }
            String code = normalize(product.getCode());
            if (code == null) {
                continue;
            }
            try {
                maxCode = Math.max(maxCode, Integer.parseInt(code));
            } catch (NumberFormatException ignored) {
                // Ignore legacy alphanumeric codes when generating the next numeric code.
            }
        }
        return String.format("%03d", maxCode + 1);
    }

    public String normalize(String code) {
        if (code == null) {
            return null;
        }
        String normalized = code.trim();
        if (normalized.isEmpty() || "null".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalized;
    }
}
