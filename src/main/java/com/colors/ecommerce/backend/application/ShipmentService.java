package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.Shipment;
import com.colors.ecommerce.backend.domain.model.ShipmentStatus;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.domain.port.IShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {
    private final IShipmentRepository shipmentRepository;
    private final IOrderRepository orderRepository;

    public ShipmentService(IShipmentRepository shipmentRepository, IOrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Shipment createOrUpdate(Shipment shipment) {
        if (shipment.getOrderId() == null) {
            throw new RuntimeException("Orden requerida");
        }
        if (shipment.getTrackingNumber() == null || shipment.getTrackingNumber().isBlank()) {
            throw new RuntimeException("Tracking obligatorio");
        }
        Order order = orderRepository.findById(shipment.getOrderId());
        if (order == null) {
            throw new RuntimeException("Orden no encontrada");
        }
        if (order.getOrderState() != OrderState.COMPLETED) {
            throw new RuntimeException("Solo se puede cargar envío en orden COMPLETED");
        }
        Shipment existing = shipmentRepository.findByOrderId(shipment.getOrderId());
        if (existing != null && existing.getStatus() == ShipmentStatus.DELIVERED) {
            throw new RuntimeException("No se puede editar un envío entregado");
        }
        if (existing != null) {
            shipment.setId(existing.getId());
            shipment.setCreatedAt(existing.getCreatedAt());
            shipment.setCreatedBy(existing.getCreatedBy());
            shipment.setUpdatedAt(LocalDateTime.now());
        } else {
            shipment.setStatus(shipment.getStatus() == null ? ShipmentStatus.CREATED : shipment.getStatus());
            shipment.setCreatedAt(LocalDateTime.now());
        }
        Shipment saved = shipmentRepository.save(shipment);
        if (saved.getStatus() == ShipmentStatus.DELIVERED) {
            order.setActualDeliveryDate(LocalDateTime.now().toLocalDate());
            orderRepository.save(order);
        }
        return saved;
    }

    @Transactional
    public Shipment updateStatus(Integer orderId, ShipmentStatus status, String updatedBy) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId);
        if (shipment == null) {
            throw new RuntimeException("Envío no encontrado");
        }
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new RuntimeException("No se puede editar un envío entregado");
        }
        shipment.setStatus(status);
        shipment.setUpdatedBy(updatedBy);
        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment saved = shipmentRepository.save(shipment);
        if (status == ShipmentStatus.DELIVERED) {
            Order order = orderRepository.findById(orderId);
            if (order != null) {
                order.setActualDeliveryDate(LocalDateTime.now().toLocalDate());
                orderRepository.save(order);
            }
        }
        return saved;
    }

    public Shipment findByOrderId(Integer orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public List<Shipment> findByUserId(Integer userId) {
        return shipmentRepository.findByUserId(userId);
    }
}
