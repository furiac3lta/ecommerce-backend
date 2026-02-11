package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.DeliveryAnalyticsService;
import com.colors.ecommerce.backend.domain.model.SaleChannel;
import com.colors.ecommerce.backend.infrastucture.rest.dto.DeliveryAlertSummary;
import com.colors.ecommerce.backend.infrastucture.rest.dto.DeliveryKpiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/deliveries")
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class DeliveryAnalyticsController {
    private final DeliveryAnalyticsService deliveryAnalyticsService;

    public DeliveryAnalyticsController(DeliveryAnalyticsService deliveryAnalyticsService) {
        this.deliveryAnalyticsService = deliveryAnalyticsService;
    }

    @GetMapping("/alerts")
    public ResponseEntity<DeliveryAlertSummary> getAlerts() {
        return ResponseEntity.ok(deliveryAnalyticsService.getAlertSummary());
    }

    @GetMapping("/kpis")
    public ResponseEntity<DeliveryKpiResponse> getKpis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) SaleChannel saleChannel
    ) {
        return ResponseEntity.ok(deliveryAnalyticsService.getKpis(from, to, saleChannel));
    }
}
