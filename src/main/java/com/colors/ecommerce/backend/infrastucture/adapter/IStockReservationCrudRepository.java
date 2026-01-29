package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.domain.model.StockReservationStatus;
import com.colors.ecommerce.backend.infrastucture.entity.StockReservationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IStockReservationCrudRepository extends CrudRepository<StockReservationEntity, Integer> {
    List<StockReservationEntity> findByOrderEntityId(Integer orderId);

    @Query("SELECT r FROM StockReservationEntity r WHERE r.orderEntity.id = :orderId AND r.status = 'ACTIVE'")
    List<StockReservationEntity> findActiveByOrderId(Integer orderId);

    @Query("SELECT COALESCE(SUM(r.qty), 0) FROM StockReservationEntity r WHERE r.variantEntity.id = :variantId AND r.status = 'ACTIVE' AND r.expiresAt > :now")
    Integer sumActiveReservedQty(Integer variantId, LocalDateTime now);

    @Query("SELECT r FROM StockReservationEntity r WHERE r.status = 'ACTIVE' AND r.expiresAt < :now")
    List<StockReservationEntity> findExpiredActive(LocalDateTime now);

    List<StockReservationEntity> findByVariantEntityId(Integer variantId);
}
