package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.DeliveryType;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.SaleChannel;
import com.colors.ecommerce.backend.domain.model.ShipmentStatus;
import com.colors.ecommerce.backend.infrastucture.adapter.IOrderCrudRepository;
import com.colors.ecommerce.backend.infrastucture.adapter.IShipmentCrudRepository;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ShipmentEntity;
import com.colors.ecommerce.backend.infrastucture.rest.dto.DeliveryAlertSummary;
import com.colors.ecommerce.backend.infrastucture.rest.dto.DeliveryKpiResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryAnalyticsService {
    private final IOrderCrudRepository orderCrudRepository;
    private final IShipmentCrudRepository shipmentCrudRepository;

    public DeliveryAnalyticsService(IOrderCrudRepository orderCrudRepository,
                                    IShipmentCrudRepository shipmentCrudRepository) {
        this.orderCrudRepository = orderCrudRepository;
        this.shipmentCrudRepository = shipmentCrudRepository;
    }

    public DeliveryAlertSummary getAlertSummary() {
        DeliveryAlertSummary summary = new DeliveryAlertSummary();
        LocalDate today = LocalDate.now();
        List<OrderEntity> orders = orderCrudRepository.findByOrderState(OrderState.COMPLETED);
        int todayCount = 0;
        int overdueCount = 0;
        for (OrderEntity order : orders) {
            if (order.getEstimatedDeliveryDate() == null) {
                continue;
            }
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            ShipmentStatus status = shipment == null ? null : shipment.getStatus();
            if (status == ShipmentStatus.DELIVERED) {
                continue;
            }
            if (order.getEstimatedDeliveryDate().isEqual(today)) {
                todayCount++;
            } else if (order.getEstimatedDeliveryDate().isBefore(today)) {
                overdueCount++;
            }
        }
        summary.setTodayCount(todayCount);
        summary.setOverdueCount(overdueCount);
        return summary;
    }

    public DeliveryKpiResponse getKpis(LocalDateTime from, LocalDateTime to, SaleChannel saleChannel) {
        DeliveryKpiResponse response = new DeliveryKpiResponse();
        LocalDate today = LocalDate.now();
        List<OrderEntity> orders = orderCrudRepository.findByDateCreatedBetween(from, to);

        long totalCompleted = 0;
        long totalWithEstimate = 0;
        long totalDelivered = 0;
        long onTime = 0;
        long overdue = 0;

        double sumEstimatedDays = 0;
        double sumActualDays = 0;
        double sumDiffDays = 0;
        long countEstimated = 0;
        long countActual = 0;
        long countDiff = 0;

        for (OrderEntity order : orders) {
            if (order.getOrderState() != OrderState.COMPLETED) {
                continue;
            }
            if (saleChannel != null && order.getSaleChannel() != saleChannel) {
                continue;
            }
            totalCompleted++;
            LocalDate estimated = order.getEstimatedDeliveryDate();
            LocalDate actual = order.getActualDeliveryDate();
            if (actual == null) {
                ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
                if (shipment != null && shipment.getStatus() == ShipmentStatus.DELIVERED) {
                    LocalDateTime stamp = shipment.getUpdatedAt() != null ? shipment.getUpdatedAt() : shipment.getCreatedAt();
                    if (stamp != null) {
                        actual = stamp.toLocalDate();
                    }
                }
            }
            if (estimated != null) {
                totalWithEstimate++;
                if (order.getDateCreated() != null) {
                    long estDays = java.time.Duration.between(order.getDateCreated().toLocalDate().atStartOfDay(), estimated.atStartOfDay()).toDays();
                    sumEstimatedDays += estDays;
                    countEstimated++;
                }
            }
            if (actual != null) {
                totalDelivered++;
                if (order.getDateCreated() != null) {
                    long actDays = java.time.Duration.between(order.getDateCreated().toLocalDate().atStartOfDay(), actual.atStartOfDay()).toDays();
                    sumActualDays += actDays;
                    countActual++;
                }
            }
            if (estimated != null && actual != null) {
                long diff = java.time.Duration.between(estimated.atStartOfDay(), actual.atStartOfDay()).toDays();
                sumDiffDays += diff;
                countDiff++;
                if (!actual.isAfter(estimated)) {
                    onTime++;
                }
            }
            if (estimated != null && actual == null && estimated.isBefore(today)) {
                overdue++;
            }
        }

        response.setTotalCompleted(totalCompleted);
        response.setTotalWithEstimate(totalWithEstimate);
        response.setTotalDelivered(totalDelivered);
        response.setAvgEstimatedDays(countEstimated == 0 ? 0 : sumEstimatedDays / countEstimated);
        response.setAvgActualDays(countActual == 0 ? 0 : sumActualDays / countActual);
        response.setAvgDiffDays(countDiff == 0 ? 0 : sumDiffDays / countDiff);
        response.setOnTimePct(totalDelivered == 0 ? 0 : (double) onTime * 100 / totalDelivered);
        response.setOverduePct(totalCompleted == 0 ? 0 : (double) overdue * 100 / totalCompleted);
        return response;
    }
}
