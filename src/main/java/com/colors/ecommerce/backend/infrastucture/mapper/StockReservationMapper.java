package com.colors.ecommerce.backend.infrastucture.mapper;

import com.colors.ecommerce.backend.domain.model.StockReservation;
import com.colors.ecommerce.backend.infrastucture.entity.StockReservationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StockReservationMapper {
    @Mappings(
            {
                    @Mapping(source = "id", target = "id"),
                    @Mapping(source = "variantEntity.id", target = "variantId"),
                    @Mapping(source = "orderEntity.id", target = "orderId"),
                    @Mapping(source = "qty", target = "qty"),
                    @Mapping(source = "expiresAt", target = "expiresAt"),
                    @Mapping(source = "createdAt", target = "createdAt"),
                    @Mapping(source = "releasedAt", target = "releasedAt"),
                    @Mapping(source = "status", target = "status")
            }
    )
    StockReservation toStockReservation(StockReservationEntity entity);

    Iterable<StockReservation> toStockReservationList(Iterable<StockReservationEntity> entities);

    @InheritInverseConfiguration
    StockReservationEntity toStockReservationEntity(StockReservation model);
}
