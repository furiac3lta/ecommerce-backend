package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.OrderService;
import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class OrderController {
    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order order) {
        order.setOrderState(OrderState.PENDING);
        if (order.getPaymentMethod() == null) {
            order.setPaymentMethod(PaymentMethod.TRANSFERENCIA);
        }
        return ResponseEntity.ok(orderService.save(order));

    }
    @PostMapping("/update/state/order")
    public ResponseEntity<Order> updateStateById(@RequestParam Integer id,
                                                 @RequestParam OrderState state,
                                                 @RequestParam(required = false) String createdBy) {
        String author = createdBy == null || createdBy.isBlank() ? "admin" : createdBy;
        return ResponseEntity.ok(orderService.updateStateById(id, state, author));

    }
    @GetMapping
    public ResponseEntity<Iterable<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }
    @GetMapping("/{variable}")
    public ResponseEntity<Order> findById(@PathVariable("variable") Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }
    @GetMapping("/by-user/{id}")
    public ResponseEntity<Iterable<Order>> findByUserId(@PathVariable("id") Integer userid) {
        return ResponseEntity.ok(orderService.findByUserId(userid));
    }
}
