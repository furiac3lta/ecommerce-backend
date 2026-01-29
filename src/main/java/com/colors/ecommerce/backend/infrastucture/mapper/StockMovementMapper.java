package com.colors.ecommerce.backend.infrastucture.mapper;

import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.infrastucture.entity.StockMovementEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    @Mappings(
            {
                    @Mapping(source = "id", target = "id"),
                    @Mapping(source = "variantEntity.id", target = "variantId"),
                    @Mapping(source = "type", target = "type"),
                    @Mapping(source = "qty", target = "qty"),
                    @Mapping(source = "reason", target = "reason"),
                    @Mapping(source = "orderEntity.id", target = "orderId"),
                    @Mapping(source = "note", target = "note"),
                    @Mapping(source = "createdAt", target = "createdAt"),
                    @Mapping(source = "createdBy", target = "createdBy")
            }
    )
    StockMovement toStockMovement(StockMovementEntity entity);

    Iterable<StockMovement> toStockMovementList(Iterable<StockMovementEntity> entities);

    @InheritInverseConfiguration
    @Mapping(target = "orderEntity", ignore = true)
    @Mapping(target = "variantEntity", ignore = true)
    StockMovementEntity toStockMovementEntity(StockMovement model);
}
