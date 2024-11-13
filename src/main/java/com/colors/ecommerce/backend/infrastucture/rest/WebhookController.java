package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhook")
@CrossOrigin(origins = {"http://localhost:4200", "https://ecommerce-angular-6bsx42ny5-furiac3ltas-projects.vercel.app/"})
public class WebhookController {

    private final OrderService orderService;

    @Autowired
    public WebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // Verificar si el tipo de evento es un pago
            String eventType = (String) payload.get("type");
            if ("payment".equals(eventType)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                String status = (String) data.get("status");

                // Supongamos que en el payload tienes un campo order_id que representa el pedido
                Integer orderId = (Integer) data.get("order_id");

                if (orderId != null) {
                    if ("approved".equals(status)) {
                        // Si el pago fue aprobado, cambia el estado del pedido a CONFIRMED
                        orderService.updateStateById(orderId, "CONFIRMED");
                    } else if ("rejected".equals(status)) {
                        // Si el pago fue rechazado, cambia el estado del pedido a CANCELLED
                        orderService.updateStateById(orderId, "CANCELLED");
                    }
                }
            }

            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar el webhook: " + e.getMessage());
        }
    }
}
