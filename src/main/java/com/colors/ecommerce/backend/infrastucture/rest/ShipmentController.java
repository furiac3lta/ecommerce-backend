package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.ShipmentService;
import com.colors.ecommerce.backend.domain.model.Shipment;
import com.colors.ecommerce.backend.domain.model.ShipmentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar", "https://ecommerce-angular-production.up.railway.app"})
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/admin/shipments")
    public ResponseEntity<Shipment> createOrUpdate(@RequestBody Shipment shipment) {
        return ResponseEntity.ok(shipmentService.createOrUpdate(shipment));
    }

    @PostMapping("/admin/shipments/status")
    public ResponseEntity<Shipment> updateStatus(@RequestParam Integer orderId,
                                                 @RequestParam ShipmentStatus status,
                                                 @RequestParam(required = false) String updatedBy) {
        String author = updatedBy == null || updatedBy.isBlank() ? "admin" : updatedBy;
        return ResponseEntity.ok(shipmentService.updateStatus(orderId, status, author));
    }

    @GetMapping("/admin/shipments/by-order/{orderId}")
    public ResponseEntity<Shipment> findByOrderAdmin(@PathVariable Integer orderId) {
        return ResponseEntity.ok(shipmentService.findByOrderId(orderId));
    }

    @GetMapping("/shipments/by-order/{orderId}")
    public ResponseEntity<Shipment> findByOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(shipmentService.findByOrderId(orderId));
    }

    @GetMapping("/shipments/by-user/{userId}")
    public ResponseEntity<List<Shipment>> findByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(shipmentService.findByUserId(userId));
    }
}
