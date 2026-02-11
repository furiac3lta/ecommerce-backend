package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.StockAdminService;
import com.colors.ecommerce.backend.application.ProductVariantService;
import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import com.colors.ecommerce.backend.domain.model.ProductVariant;
import com.colors.ecommerce.backend.domain.model.StockReservation;
import com.colors.ecommerce.backend.infrastucture.rest.dto.StockAdjustRequest;
import com.colors.ecommerce.backend.infrastucture.rest.dto.StockMovementRequest;
import com.colors.ecommerce.backend.infrastucture.rest.dto.StockRestockRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar", "https://ecommerce-angular-production.up.railway.app"})
@RequestMapping("/api/v1/admin/stock")
public class StockAdminController {
    private final StockAdminService stockAdminService;
    private final ProductVariantService productVariantService;

    public StockAdminController(StockAdminService stockAdminService, ProductVariantService productVariantService) {
        this.stockAdminService = stockAdminService;
        this.productVariantService = productVariantService;
    }

    @GetMapping("/variants")
    public ResponseEntity<Iterable<ProductVariant>> getVariants() {
        return ResponseEntity.ok(productVariantService.findAll());
    }

    @GetMapping("/movements")
    public ResponseEntity<Iterable<StockMovement>> getMovements(
            @RequestParam(required = false) Integer variantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) StockMovementType type,
            @RequestParam(required = false) StockMovementReason reason
    ) {
        LocalDateTime fromDate = from == null ? LocalDateTime.now().minusDays(30) : from;
        LocalDateTime toDate = to == null ? LocalDateTime.now() : to;
        return ResponseEntity.ok(stockAdminService.getMovements(variantId, fromDate, toDate, type, reason));
    }

    @PostMapping("/movements")
    public ResponseEntity<StockMovement> createMovement(@RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(
                stockAdminService.createMovement(
                        request.getVariantId(),
                        request.getQty(),
                        request.getType(),
                        request.getReason(),
                        request.getNote(),
                        request.getCreatedBy()
                )
        );
    }

    @PostMapping("/restock")
    public ResponseEntity<StockMovement> restock(@RequestBody StockRestockRequest request) {
        return ResponseEntity.ok(stockAdminService.restock(request.getVariantId(), request.getQty(), request.getNote(), request.getCreatedBy()));
    }

    @PostMapping("/adjust")
    public ResponseEntity<StockMovement> adjust(@RequestBody StockAdjustRequest request) {
        return ResponseEntity.ok(stockAdminService.adjust(request.getVariantId(), request.getNewStock(), request.getNote(), request.getCreatedBy()));
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<StockReservation>> getReservations(@RequestParam Integer variantId) {
        return ResponseEntity.ok(stockAdminService.getReservations(variantId));
    }

    @PostMapping("/reservations/release")
    public ResponseEntity<Void> releaseReservations(@RequestParam Integer orderId) {
        stockAdminService.releaseReservationsByOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
