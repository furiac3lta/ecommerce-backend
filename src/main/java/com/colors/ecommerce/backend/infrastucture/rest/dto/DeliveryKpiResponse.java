package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

@Data
public class DeliveryKpiResponse {
    private double avgEstimatedDays;
    private double avgActualDays;
    private double avgDiffDays;
    private double onTimePct;
    private double overduePct;
    private long totalCompleted;
    private long totalWithEstimate;
    private long totalDelivered;
}
