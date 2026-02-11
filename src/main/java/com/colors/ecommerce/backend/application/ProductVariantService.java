package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.ProductVariant;
import com.colors.ecommerce.backend.domain.port.IProductVariantRepository;
import com.colors.ecommerce.backend.domain.port.IStockReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductVariantService {
    private final IProductVariantRepository productVariantRepository;
    private final IStockReservationRepository stockReservationRepository;

    public ProductVariantService(IProductVariantRepository productVariantRepository,
                                 IStockReservationRepository stockReservationRepository) {
        this.productVariantRepository = productVariantRepository;
        this.stockReservationRepository = stockReservationRepository;
    }

    public ProductVariant save(ProductVariant productVariant) {
        if (productVariant.getActive() == null) {
            productVariant.setActive(true);
        }
        if (productVariant.getStockCurrent() == null) {
            productVariant.setStockCurrent(0);
        }
        if (productVariant.getStockMinimum() == null) {
            productVariant.setStockMinimum(0);
        }
        if (productVariant.getSellOnline() == null) {
            productVariant.setSellOnline(true);
        }
        if (productVariant.getDeliveryType() == null) {
            productVariant.setDeliveryType(com.colors.ecommerce.backend.domain.model.DeliveryType.IMMEDIATE);
        }
        return productVariantRepository.save(productVariant);
    }

    public Iterable<ProductVariant> findAll() {
        Iterable<ProductVariant> variants = productVariantRepository.findAll();
        variants.forEach(this::applyAvailability);
        return variants;
    }

    public Iterable<ProductVariant> findByProductId(Integer productId) {
        Iterable<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        variants.forEach(this::applyAvailability);
        return variants;
    }

    public ProductVariant findById(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id);
        applyAvailability(variant);
        return variant;
    }

    public void deleteById(Integer id) {
        productVariantRepository.deleteById(id);
    }

    private void applyAvailability(ProductVariant variant) {
        Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
        Integer reserved = stockReservationRepository.sumActiveReservedQty(variant.getId(), LocalDateTime.now());
        variant.setReservedStock(reserved);
        variant.setAvailableStock(Math.max(0, currentStock - reserved));
    }
}
