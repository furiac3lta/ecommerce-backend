package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.ProductVariant;
import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import com.colors.ecommerce.backend.domain.model.StockReservation;
import com.colors.ecommerce.backend.domain.model.StockReservationStatus;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.domain.port.IProductVariantRepository;
import com.colors.ecommerce.backend.domain.port.IStockMovementRepository;
import com.colors.ecommerce.backend.domain.port.IStockReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockAdminService {
    private final IProductVariantRepository productVariantRepository;
    private final IStockMovementRepository stockMovementRepository;
    private final IStockReservationRepository stockReservationRepository;
    private final IOrderRepository orderRepository;

    public StockAdminService(IProductVariantRepository productVariantRepository,
                             IStockMovementRepository stockMovementRepository,
                             IStockReservationRepository stockReservationRepository,
                             IOrderRepository orderRepository) {
        this.productVariantRepository = productVariantRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.stockReservationRepository = stockReservationRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public StockMovement restock(Integer variantId, Integer qty, String note, String createdBy) {
        if (qty == null || qty <= 0) {
            throw new RuntimeException("Cantidad inválida para restock");
        }
        if (note == null || note.isBlank()) {
            throw new RuntimeException("Nota obligatoria para restock");
        }
        ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId);
        Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
        variant.setStockCurrent(currentStock + qty);
        productVariantRepository.save(variant);

        StockMovement movement = new StockMovement();
        movement.setVariantId(variantId);
        movement.setType(StockMovementType.IN);
        movement.setQty(qty);
        movement.setReason(StockMovementReason.RESTOCK);
        movement.setNote(note);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setCreatedBy(createdBy);
        return stockMovementRepository.save(movement);
    }

    @Transactional
    public StockMovement adjust(Integer variantId, Integer newStock, String note, String createdBy) {
        if (note == null || note.isBlank()) {
            throw new RuntimeException("Nota obligatoria para ajuste");
        }
        if (newStock == null || newStock < 0) {
            throw new RuntimeException("Stock inválido");
        }
        ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId);
        Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
        int delta = newStock - currentStock;
        variant.setStockCurrent(newStock);
        productVariantRepository.save(variant);

        StockMovement movement = new StockMovement();
        movement.setVariantId(variantId);
        movement.setType(StockMovementType.ADJUST);
        movement.setQty(Math.abs(delta));
        movement.setReason(StockMovementReason.MANUAL_ADJUST);
        movement.setNote(note);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setCreatedBy(createdBy);
        return stockMovementRepository.save(movement);
    }

    @Transactional
    public StockMovement createMovement(Integer variantId, Integer qty, StockMovementType type, StockMovementReason reason, String note, String createdBy) {
        if (variantId == null) {
            throw new RuntimeException("Variante requerida");
        }
        if (qty == null || qty <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }
        if (type == null) {
            throw new RuntimeException("Tipo requerido");
        }
        if (reason == null) {
            throw new RuntimeException("Motivo requerido");
        }
        if (note == null || note.isBlank()) {
            throw new RuntimeException("Nota obligatoria");
        }
        ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId);
        Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
        if (type == StockMovementType.OUT) {
            if (currentStock - qty < 0) {
                throw new RuntimeException("No se permite stock negativo");
            }
            variant.setStockCurrent(currentStock - qty);
        } else if (type == StockMovementType.IN) {
            variant.setStockCurrent(currentStock + qty);
        } else if (type == StockMovementType.ADJUST) {
            variant.setStockCurrent(Math.max(0, currentStock + qty));
        }
        productVariantRepository.save(variant);

        StockMovement movement = new StockMovement();
        movement.setVariantId(variantId);
        movement.setType(type);
        movement.setQty(qty);
        movement.setReason(reason);
        movement.setNote(note);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setCreatedBy(createdBy);
        return stockMovementRepository.save(movement);
    }

    public Iterable<StockMovement> getMovements(Integer variantId, LocalDateTime from, LocalDateTime to) {
        return stockMovementRepository.findByVariantIdAndDateRange(variantId, from, to);
    }

    public Iterable<StockMovement> getMovements(Integer variantId, LocalDateTime from, LocalDateTime to, StockMovementType type, StockMovementReason reason) {
        return stockMovementRepository.findByFilters(variantId, from, to, type, reason);
    }

    public List<StockReservation> getReservations(Integer variantId) {
        return stockReservationRepository.findByVariantId(variantId);
    }

    @Transactional
    public void releaseReservationsByOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId);
        if (order.getOrderState() == OrderState.COMPLETED) {
            throw new RuntimeException("No se puede liberar una orden COMPLETED");
        }
        List<StockReservation> reservations = stockReservationRepository.findActiveByOrderId(orderId);
        LocalDateTime now = LocalDateTime.now();
        for (StockReservation reservation : reservations) {
            reservation.setStatus(StockReservationStatus.RELEASED);
            reservation.setReleasedAt(now);
            stockReservationRepository.save(reservation);
        }
        if (order.getOrderState() == OrderState.PENDING) {
            order.setOrderState(OrderState.CANCELLED);
            orderRepository.save(order);
        }
    }
}
