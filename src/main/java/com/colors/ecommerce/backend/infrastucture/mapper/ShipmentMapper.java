package com.colors.ecommerce.backend.infrastucture.mapper;

import com.colors.ecommerce.backend.domain.model.Shipment;
import com.colors.ecommerce.backend.infrastucture.entity.ShipmentEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    @Mappings(
            {
                    @Mapping(source = "id", target = "id"),
                    @Mapping(source = "orderEntity.id", target = "orderId"),
                    @Mapping(source = "carrier", target = "carrier"),
                    @Mapping(source = "trackingNumber", target = "trackingNumber"),
                    @Mapping(source = "shippingMethod", target = "shippingMethod"),
                    @Mapping(source = "recipientName", target = "recipientName"),
                    @Mapping(source = "recipientPhone", target = "recipientPhone"),
                    @Mapping(source = "recipientAddress", target = "recipientAddress"),
                    @Mapping(source = "notes", target = "notes"),
                    @Mapping(source = "status", target = "status"),
                    @Mapping(source = "createdAt", target = "createdAt"),
                    @Mapping(source = "updatedAt", target = "updatedAt"),
                    @Mapping(source = "createdBy", target = "createdBy"),
                    @Mapping(source = "updatedBy", target = "updatedBy")
            }
    )
    Shipment toShipment(ShipmentEntity entity);

    java.util.List<Shipment> toShipmentList(java.util.List<ShipmentEntity> entities);

    @InheritInverseConfiguration
    ShipmentEntity toShipmentEntity(Shipment model);
}
