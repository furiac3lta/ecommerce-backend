package com.colors.ecommerce.backend.infrastucture.rest;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;

import com.mercadopago.resources.preference.Preference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @PostMapping("/create-preference")
    public ResponseEntity<String> createPreference(@RequestParam("amount") BigDecimal amount, @RequestParam("description") String description) {
        try {
            // Crear el item de la preferencia
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(description)
                    .quantity(1)
                    .unitPrice(amount)
                    .build();

            // Crear la preferencia
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(Collections.singletonList(item))
                    .build();

            // Enviar la solicitud a Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(request);

            // Retornar la URL de redirección de Mercado Pago
            return ResponseEntity.ok(preference.getInitPoint());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear la preferencia de pago");
        }
    }

}
