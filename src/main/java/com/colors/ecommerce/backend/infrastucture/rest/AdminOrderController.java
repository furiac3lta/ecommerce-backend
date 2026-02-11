package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.OrderService;
import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.SaleChannel;
import com.colors.ecommerce.backend.infrastucture.rest.dto.TimelineEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class AdminOrderController {
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/wholesale")
    public ResponseEntity<Order> createWholesale(@RequestBody Order order) {
        order.setSaleChannel(SaleChannel.WHOLESALE);
        return ResponseEntity.ok(orderService.save(order));
    }

    @PostMapping("/offline")
    public ResponseEntity<Order> createOffline(@RequestBody Order order) {
        order.setSaleChannel(SaleChannel.OFFLINE);
        return ResponseEntity.ok(orderService.save(order));
    }

    @GetMapping
    public ResponseEntity<Iterable<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{orderId}/timeline")
    public ResponseEntity<java.util.List<TimelineEventDto>> getTimeline(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.buildTimeline(orderId));
    }

    @PostMapping("/update/state/order")
    public ResponseEntity<Order> updateStateById(@RequestParam Integer id,
                                                 @RequestParam OrderState state,
                                                 @RequestParam(required = false) String createdBy) {
        String author = createdBy == null || createdBy.isBlank() ? "admin" : createdBy;
        return ResponseEntity.ok(orderService.updateStateById(id, state, author));
    }
}
