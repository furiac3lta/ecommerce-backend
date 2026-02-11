package com.colors.ecommerce.backend.infrastucture.mapper;

import com.colors.ecommerce.backend.domain.model.ProductVariant;
import com.colors.ecommerce.backend.infrastucture.entity.ProductVariantEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    @Mappings(
            {
                    @Mapping(source = "id", target = "id"),
                    @Mapping(source = "productEntity.id", target = "productId"),
                    @Mapping(source = "size", target = "size"),
                    @Mapping(source = "color", target = "color"),
                    @Mapping(source = "gsm", target = "gsm"),
                    @Mapping(source = "material", target = "material"),
                    @Mapping(source = "usage", target = "usage"),
                    @Mapping(source = "sku", target = "sku"),
                    @Mapping(source = "priceRetail", target = "priceRetail"),
                    @Mapping(source = "priceWholesale", target = "priceWholesale"),
                    @Mapping(source = "deliveryType", target = "deliveryType"),
                    @Mapping(source = "estimatedDeliveryDays", target = "estimatedDeliveryDays"),
                    @Mapping(source = "estimatedDeliveryDate", target = "estimatedDeliveryDate"),
                    @Mapping(source = "deliveryNote", target = "deliveryNote"),
                    @Mapping(source = "stockCurrent", target = "stockCurrent"),
                    @Mapping(source = "stockMinimum", target = "stockMinimum"),
                    @Mapping(source = "active", target = "active"),
                    @Mapping(source = "sellOnline", target = "sellOnline"),
                    @Mapping(source = "productEntity.name", target = "productName")
            }
    )
    ProductVariant toProductVariant(ProductVariantEntity entity);

    Iterable<ProductVariant> toProductVariantList(Iterable<ProductVariantEntity> entities);

    @InheritInverseConfiguration
    ProductVariantEntity toProductVariantEntity(ProductVariant model);
}
