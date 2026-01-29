package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.ShipmentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface IShipmentCrudRepository extends CrudRepository<ShipmentEntity, Integer> {
    Optional<ShipmentEntity> findByOrderEntityId(Integer orderId);
    List<ShipmentEntity> findByOrderEntityUserEntityId(Integer userId);
}
