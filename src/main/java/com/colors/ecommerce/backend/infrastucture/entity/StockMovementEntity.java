package com.colors.ecommerce.backend.infrastucture.entity;

import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
public class StockMovementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private ProductVariantEntity variantEntity;

    @Enumerated(EnumType.STRING)
    private StockMovementType type;

    private Integer qty;

    @Enumerated(EnumType.STRING)
    private StockMovementReason reason;

    @ManyToOne
    private OrderEntity orderEntity;

    private String note;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String createdBy;
}
