package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderProduct;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.PaymentMethod;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

public class OrderService {
    private final IOrderRepository IOrderRepository;
    private final IProductVariantRepository productVariantRepository;
    private final IStockReservationRepository stockReservationRepository;
    private final IStockMovementRepository stockMovementRepository;

    public OrderService(IOrderRepository iOrderRepository,
                        IProductVariantRepository productVariantRepository,
                        IStockReservationRepository stockReservationRepository,
                        IStockMovementRepository stockMovementRepository) {
        this.IOrderRepository = iOrderRepository;
        this.productVariantRepository = productVariantRepository;
        this.stockReservationRepository = stockReservationRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public Order save(Order order) {
        System.out.println("Guardando orden: " + order);
        if (order.getOrderState() == null) {
            order.setOrderState(OrderState.PENDING);
        }
        if (order.getPaymentMethod() == null) {
            order.setPaymentMethod(PaymentMethod.TRANSFERENCIA);
        }
        if (order.getTotal() == null) {
            order.setTotal(order.getTotalOrderPrice());
        }
        Order saved = this.IOrderRepository.save(order);
        if (saved.getOrderState() == OrderState.PENDING) {
            reserveStock(saved);
        }
        return saved;
    }
    public Iterable<Order> findAll() {
        return this.IOrderRepository.findAll();
    }
    public Iterable<Order> findByUserId(Integer userId) {
        return this.IOrderRepository.findByUserId(userId);
    }
    @Transactional
    public Order updateStateById(Integer id, OrderState state) {
        return updateStateById(id, state, "admin");
    }

    @Transactional
    public Order updateStateById(Integer id, OrderState state, String createdBy) {
        Order existingOrder = this.IOrderRepository.findById(id);
        if (existingOrder.getOrderState() == OrderState.COMPLETED) {
            return existingOrder;
        }

        existingOrder.setOrderState(state);
        if (state == OrderState.COMPLETED) {
            consumeReservations(existingOrder, createdBy);
            existingOrder.setPaidAt(LocalDateTime.now());
        } else if (state == OrderState.CANCELLED) {
            releaseReservations(existingOrder);
        }
        return this.IOrderRepository.save(existingOrder);
    }
    public Order findById(Integer id) {
        return this.IOrderRepository.findById(id);
    }

    private void reserveStock(Order order) {
        List<StockReservation> existing = stockReservationRepository.findByOrderId(order.getId());
        if (existing != null && !existing.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Integer variantId = orderProduct.getProductVariantId();
            Integer qty = orderProduct.getQuantity() == null ? 0 : orderProduct.getQuantity().intValue();
            if (qty <= 0) {
                continue;
            }
            ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId);
            Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
            Integer reserved = stockReservationRepository.sumActiveReservedQty(variantId, now);
            int available = currentStock - reserved;
            if (available < qty) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para la variante " + variantId);
            }

            StockReservation reservation = new StockReservation();
            reservation.setVariantId(variantId);
            reservation.setOrderId(order.getId());
            reservation.setQty(qty);
            reservation.setCreatedAt(now);
            reservation.setExpiresAt(now.plusMinutes(30));
            reservation.setStatus(StockReservationStatus.ACTIVE);
            stockReservationRepository.save(reservation);
        }
    }

    private void consumeReservations(Order order, String createdBy) {
        List<StockReservation> reservations = stockReservationRepository.findActiveByOrderId(order.getId());
        if (reservations == null || reservations.isEmpty()) {
            // Fallback: si no hay reservas activas, consumimos según productos de la orden
            if (order.getOrderProducts() == null || order.getOrderProducts().isEmpty()) {
                throw new RuntimeException("No hay reservas ni productos para la orden " + order.getId());
            }
            LocalDateTime now = LocalDateTime.now();
            for (OrderProduct orderProduct : order.getOrderProducts()) {
                Integer variantId = orderProduct.getProductVariantId();
                Integer qty = orderProduct.getQuantity() == null ? 0 : orderProduct.getQuantity().intValue();
                if (qty <= 0) {
                    continue;
                }
                ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId);
                Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
                if (currentStock < qty) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para confirmar la orden " + order.getId());
                }
                variant.setStockCurrent(currentStock - qty);
                productVariantRepository.save(variant);

                StockMovement movement = new StockMovement();
                movement.setVariantId(variantId);
                movement.setType(StockMovementType.OUT);
                movement.setQty(qty);
                movement.setReason(StockMovementReason.SALE);
                movement.setOrderId(order.getId());
                movement.setCreatedAt(now);
                movement.setCreatedBy(createdBy);
                stockMovementRepository.save(movement);
            }
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (StockReservation reservation : reservations) {
            ProductVariant variant = productVariantRepository.findByIdForUpdate(reservation.getVariantId());
            Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
            Integer qty = reservation.getQty() == null ? 0 : reservation.getQty();
            if (currentStock < qty) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para confirmar la orden " + order.getId());
            }
            variant.setStockCurrent(currentStock - qty);
            productVariantRepository.save(variant);

            reservation.setStatus(StockReservationStatus.CONSUMED);
            reservation.setReleasedAt(now);
            stockReservationRepository.save(reservation);

            StockMovement movement = new StockMovement();
            movement.setVariantId(reservation.getVariantId());
            movement.setType(StockMovementType.OUT);
            movement.setQty(qty);
            movement.setReason(StockMovementReason.SALE);
            movement.setOrderId(order.getId());
            movement.setCreatedAt(now);
            movement.setCreatedBy(createdBy);
            stockMovementRepository.save(movement);
        }
    }

    private void releaseReservations(Order order) {
        List<StockReservation> reservations = stockReservationRepository.findActiveByOrderId(order.getId());
        if (reservations == null || reservations.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (StockReservation reservation : reservations) {
            reservation.setStatus(StockReservationStatus.RELEASED);
            reservation.setReleasedAt(now);
            stockReservationRepository.save(reservation);
        }
    }
}
