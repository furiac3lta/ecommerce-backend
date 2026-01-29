package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;

import java.time.LocalDateTime;

public interface IStockMovementRepository {
    StockMovement save(StockMovement movement);
    Iterable<StockMovement> findByVariantIdAndDateRange(Integer variantId, LocalDateTime from, LocalDateTime to);
    Iterable<StockMovement> findByFilters(Integer variantId, LocalDateTime from, LocalDateTime to, StockMovementType type, StockMovementReason reason);
}
