package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

@Data
public class DeliveryAlertSummary {
    private int todayCount;
    private int overdueCount;
}
