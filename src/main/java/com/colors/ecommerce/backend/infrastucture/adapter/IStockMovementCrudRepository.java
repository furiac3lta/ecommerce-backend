package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.StockMovementEntity;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IStockMovementCrudRepository extends CrudRepository<StockMovementEntity, Integer> {
    @Query("SELECT m FROM StockMovementEntity m WHERE (:variantId IS NULL OR m.variantEntity.id = :variantId) AND m.createdAt BETWEEN :from AND :to ORDER BY m.createdAt DESC")
    List<StockMovementEntity> findByVariantAndRange(Integer variantId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT m FROM StockMovementEntity m WHERE (:variantId IS NULL OR m.variantEntity.id = :variantId) AND (:type IS NULL OR m.type = :type) AND (:reason IS NULL OR m.reason = :reason) AND m.createdAt BETWEEN :from AND :to ORDER BY m.createdAt DESC")
    List<StockMovementEntity> findByFilters(Integer variantId, LocalDateTime from, LocalDateTime to, StockMovementType type, StockMovementReason reason);

    @Query("SELECT m FROM StockMovementEntity m WHERE m.orderEntity.id = :orderId ORDER BY m.createdAt DESC")
    List<StockMovementEntity> findByOrderId(Integer orderId);

    @Query("SELECT m FROM StockMovementEntity m WHERE m.createdAt BETWEEN :from AND :to ORDER BY m.createdAt DESC")
    List<StockMovementEntity> findByDateRange(LocalDateTime from, LocalDateTime to);
}
