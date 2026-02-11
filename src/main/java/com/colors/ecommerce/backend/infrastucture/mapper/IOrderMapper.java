package com.colors.ecommerce.backend.infrastucture.mapper;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring", uses = {IOrderProductMapper.class})
public interface IOrderMapper {
    @Mappings(
            {
                    @Mapping(source = "id", target = "id"),
                    @Mapping(source = "dateCreated", target = "dateCreated"),
                    @Mapping(source = "orderProducts", target = "orderProducts"),
                    @Mapping(source = "orderState", target = "orderState"),
                    @Mapping(source = "paymentMethod", target = "paymentMethod"),
                    @Mapping(source = "total", target = "total"),
                    @Mapping(source = "paidAt", target = "paidAt"),
                    @Mapping(source = "userEntity.id", target = "userId"),
                    @Mapping(source = "saleChannel", target = "saleChannel"),
                    @Mapping(source = "orderNumber", target = "orderNumber"),
                    @Mapping(source = "deliveryType", target = "deliveryType"),
                    @Mapping(source = "estimatedDeliveryDate", target = "estimatedDeliveryDate"),
                    @Mapping(source = "actualDeliveryDate", target = "actualDeliveryDate"),
                    @Mapping(source = "balanceDue", target = "balanceDue"),
                    @Mapping(source = "balanceCredit", target = "balanceCredit")
            }
    )
    Order toOrder(OrderEntity orderEntity);
    Iterable<Order> toOrderList(Iterable<OrderEntity> orderEntities);

    @InheritInverseConfiguration
    OrderEntity toOrderEntity(Order order);

}
