package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.OrderService;
import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ExchangeRequest;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ReturnRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar", "https://ecommerce-angular-production.up.railway.app"})
public class OrderAdjustmentController {
    private final OrderService orderService;

    public OrderAdjustmentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/returns")
    public ResponseEntity<Order> createReturn(@RequestBody ReturnRequest request) {
        return ResponseEntity.ok(orderService.processReturn(request));
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Order> createExchange(@RequestBody ExchangeRequest request) {
        return ResponseEntity.ok(orderService.processExchange(request));
    }

    @GetMapping("/{orderId}/movements")
    public ResponseEntity<Iterable<StockMovement>> getMovements(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getMovementsByOrder(orderId));
    }

}
