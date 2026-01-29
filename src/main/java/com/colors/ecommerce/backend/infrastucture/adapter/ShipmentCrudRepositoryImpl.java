package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.domain.model.Shipment;
import com.colors.ecommerce.backend.domain.port.IShipmentRepository;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ShipmentEntity;
import com.colors.ecommerce.backend.infrastucture.mapper.ShipmentMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShipmentCrudRepositoryImpl implements IShipmentRepository {
    private final IShipmentCrudRepository shipmentCrudRepository;
    private final ShipmentMapper shipmentMapper;

    public ShipmentCrudRepositoryImpl(IShipmentCrudRepository shipmentCrudRepository, ShipmentMapper shipmentMapper) {
        this.shipmentCrudRepository = shipmentCrudRepository;
        this.shipmentMapper = shipmentMapper;
    }

    @Override
    public Shipment save(Shipment shipment) {
        ShipmentEntity entity = shipmentMapper.toShipmentEntity(shipment);
        if (shipment.getOrderId() != null) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setId(shipment.getOrderId());
            entity.setOrderEntity(orderEntity);
        }
        return shipmentMapper.toShipment(shipmentCrudRepository.save(entity));
    }

    @Override
    public Shipment findByOrderId(Integer orderId) {
        return shipmentCrudRepository.findByOrderEntityId(orderId)
                .map(shipmentMapper::toShipment)
                .orElse(null);
    }

    @Override
    public Shipment findById(Integer id) {
        return shipmentCrudRepository.findById(id)
                .map(shipmentMapper::toShipment)
                .orElse(null);
    }

    @Override
    public List<Shipment> findByUserId(Integer userId) {
        return shipmentMapper.toShipmentList(shipmentCrudRepository.findByOrderEntityUserEntityId(userId));
    }

    @Override
    public List<Shipment> findAll() {
        List<ShipmentEntity> entities = new java.util.ArrayList<>();
        shipmentCrudRepository.findAll().forEach(entities::add);
        return shipmentMapper.toShipmentList(entities);
    }
}
