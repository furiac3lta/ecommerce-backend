package com.colors.ecommerce.backend.infrastucture.entity;

import com.colors.ecommerce.backend.domain.model.StockReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_reservations")
@Data
@NoArgsConstructor
public class StockReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private ProductVariantEntity variantEntity;

    @ManyToOne
    private OrderEntity orderEntity;

    private Integer qty;
    private LocalDateTime expiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime releasedAt;

    @Enumerated(EnumType.STRING)
    private StockReservationStatus status;
}
