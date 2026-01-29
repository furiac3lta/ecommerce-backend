package com.colors.ecommerce.backend.infrastucture.entity;

import com.colors.ecommerce.backend.domain.model.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
public class ShipmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private OrderEntity orderEntity;

    private String carrier;
    private String trackingNumber;
    private String shippingMethod;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String notes;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}
