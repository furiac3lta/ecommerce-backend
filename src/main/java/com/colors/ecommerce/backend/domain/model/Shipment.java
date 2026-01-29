package com.colors.ecommerce.backend.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Shipment {
    private Integer id;
    private Integer orderId;
    private String carrier;
    private String trackingNumber;
    private String shippingMethod;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String notes;
    private ShipmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
