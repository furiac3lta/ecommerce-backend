package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.Shipment;

import java.util.List;

public interface IShipmentRepository {
    Shipment save(Shipment shipment);
    Shipment findByOrderId(Integer orderId);
    Shipment findById(Integer id);
    List<Shipment> findByUserId(Integer userId);
    List<Shipment> findAll();
}
