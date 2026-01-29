package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.StockReservation;
import com.colors.ecommerce.backend.domain.model.StockReservationStatus;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.domain.port.IStockReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StockReservationCleanupJob {
    private final IStockReservationRepository stockReservationRepository;
    private final IOrderRepository orderRepository;

    public StockReservationCleanupJob(IStockReservationRepository stockReservationRepository,
                                      IOrderRepository orderRepository) {
        this.stockReservationRepository = stockReservationRepository;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<StockReservation> expired = stockReservationRepository.findExpiredActive(now);
        for (StockReservation reservation : expired) {
            reservation.setStatus(StockReservationStatus.RELEASED);
            reservation.setReleasedAt(now);
            stockReservationRepository.save(reservation);

            if (reservation.getOrderId() != null) {
                Order order = orderRepository.findById(reservation.getOrderId());
                if (order.getOrderState() == OrderState.PENDING) {
                    order.setOrderState(OrderState.CANCELLED);
                    orderRepository.save(order);
                }
            }
        }
    }
}
