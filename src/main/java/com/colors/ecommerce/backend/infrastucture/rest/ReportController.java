package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.ReportService;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import com.colors.ecommerce.backend.domain.model.ShipmentStatus;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/reports")
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales.pdf")
    public ResponseEntity<ByteArrayResource> salesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) OrderState state
    ) {
        OrderState orderState = state == null ? OrderState.COMPLETED : state;
        byte[] bytes = reportService.salesReport(from, to, orderState);
        return pdfResponse(bytes, "sales-report.pdf");
    }

    @GetMapping("/stock.pdf")
    public ResponseEntity<ByteArrayResource> stockReport(
            @RequestParam(required = false) Integer variantId,
            @RequestParam(required = false) StockMovementType type,
            @RequestParam(required = false) StockMovementReason reason,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        LocalDateTime fromDate = from == null ? LocalDateTime.now().minusDays(30) : from;
        LocalDateTime toDate = to == null ? LocalDateTime.now() : to;
        byte[] bytes = reportService.stockReport(fromDate, toDate, variantId, type, reason);
        return pdfResponse(bytes, "stock-report.pdf");
    }

    @GetMapping("/kardex.pdf")
    public ResponseEntity<ByteArrayResource> kardexReport(
            @RequestParam Integer variantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        byte[] bytes = reportService.kardexReport(variantId, from, to);
        return pdfResponse(bytes, "kardex.pdf");
    }

    @GetMapping("/orders-shipments.pdf")
    public ResponseEntity<ByteArrayResource> ordersShipmentsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) String carrier
    ) {
        byte[] bytes = reportService.ordersShipmentsReport(from, to, status, carrier);
        return pdfResponse(bytes, "orders-shipments.pdf");
    }

    private ResponseEntity<ByteArrayResource> pdfResponse(byte[] bytes, String filename) {
        ByteArrayResource resource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
