package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.StockReservation;
import com.colors.ecommerce.backend.domain.model.StockReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface IStockReservationRepository {
    StockReservation save(StockReservation reservation);
    List<StockReservation> findByOrderId(Integer orderId);
    List<StockReservation> findActiveByOrderId(Integer orderId);
    Integer sumActiveReservedQty(Integer variantId, LocalDateTime now);
    List<StockReservation> findExpiredActive(LocalDateTime now);
    List<StockReservation> findByVariantId(Integer variantId);
}
