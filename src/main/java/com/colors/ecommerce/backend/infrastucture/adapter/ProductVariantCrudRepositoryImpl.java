package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.domain.model.ProductVariant;
import com.colors.ecommerce.backend.domain.port.IProductVariantRepository;
import com.colors.ecommerce.backend.infrastucture.entity.ProductEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ProductVariantEntity;
import com.colors.ecommerce.backend.infrastucture.mapper.ProductVariantMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProductVariantCrudRepositoryImpl implements IProductVariantRepository {
    private final ProductVariantMapper productVariantMapper;
    private final IProductVariantCrudRepository productVariantCrudRepository;

    public ProductVariantCrudRepositoryImpl(ProductVariantMapper productVariantMapper, IProductVariantCrudRepository productVariantCrudRepository) {
        this.productVariantMapper = productVariantMapper;
        this.productVariantCrudRepository = productVariantCrudRepository;
    }

    @Override
    public ProductVariant save(ProductVariant productVariant) {
        ProductVariantEntity entity = productVariantMapper.toProductVariantEntity(productVariant);
        if (productVariant.getProductId() != null) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setId(productVariant.getProductId());
            entity.setProductEntity(productEntity);
        }
        return productVariantMapper.toProductVariant(productVariantCrudRepository.save(entity));
    }

    @Override
    public Iterable<ProductVariant> findAll() {
        return productVariantMapper.toProductVariantList(productVariantCrudRepository.findAll());
    }

    @Override
    public Iterable<ProductVariant> findByProductId(Integer productId) {
        return productVariantMapper.toProductVariantList(productVariantCrudRepository.findByProductEntityId(productId));
    }

    @Override
    public ProductVariant findById(Integer id) {
        return productVariantMapper.toProductVariant(productVariantCrudRepository.findById(id).orElseThrow(
                () -> new RuntimeException("ProductVariant with id " + id + " not found")
        ));
    }

    @Override
    public ProductVariant findByIdForUpdate(Integer id) {
        ProductVariantEntity entity = productVariantCrudRepository.findByIdForUpdate(id);
        if (entity == null) {
            throw new RuntimeException("ProductVariant with id " + id + " not found");
        }
        return productVariantMapper.toProductVariant(entity);
    }

    @Override
    public void deleteById(Integer id) {
        productVariantCrudRepository.deleteById(id);
    }
}
