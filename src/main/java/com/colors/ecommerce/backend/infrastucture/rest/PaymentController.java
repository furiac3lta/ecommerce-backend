//package com.colors.ecommerce.backend.infrastucture.rest;
//
//import com.mercadopago.client.preference.PreferenceClient;
//import com.mercadopago.client.preference.PreferenceItemRequest;
//import com.mercadopago.client.preference.PreferenceRequest;
//
//import com.mercadopago.resources.preference.Preference;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//
//@RestController
//@RequestMapping("/api/payments")
//@CrossOrigin(origins = "http://localhost:4200")
//public class PaymentController {
//
//    @PostMapping("/create-preference")
//    public ResponseEntity<String> createPreference(@RequestParam("amount") BigDecimal amount, @RequestParam("description") String description) {
//        try {
//        //    MercadoPago.SDK.setAccessToken("YOUR_PRODUCTION_ACCESS_TOKEN");
//            // Crear el item de la preferencia
//            PreferenceItemRequest item = PreferenceItemRequest.builder()
//                    .title(description)
//                    .quantity(1)
//                    .unitPrice(amount)
//                    .build();
//
//            // Crear la preferencia
//            PreferenceRequest request = PreferenceRequest.builder()
//                    .items(Collections.singletonList(item))
//                    .build();
//
//            // Enviar la solicitud a Mercado Pago
//            PreferenceClient client = new PreferenceClient();
//            Preference preference = client.create(request);
//
//            // Retornar la URL de redirección de Mercado Pago
//            return ResponseEntity.ok(preference.getInitPoint());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error al crear la preferencia de pago");
//        }
//    }
//
//}
package com.colors.ecommerce.backend.infrastucture.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:4200", "https://ecommerce-angular-five.vercel.app"})
public class PaymentController {

    @Value("${mercadopago.access_token}")
    private String accessToken;

//    @PostMapping("/create-preference")
//    public ResponseEntity<String> createPreference(@RequestParam("amount") BigDecimal amount, @RequestParam("description") String description) {
//        try {
//            // Crear el item de la preferencia
//            Map<String, Object> item = new HashMap<>();
//            item.put("title", description);
//            item.put("quantity", 1);
//            item.put("unit_price", amount);
//
//            // Crear la preferencia
//            Map<String, Object> preferenceData = new HashMap<>();
//            preferenceData.put("items", Collections.singletonList(item));
//
//            // Configurar RestTemplate
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(accessToken);
//
//            HttpEntity<Map<String, Object>> request = new HttpEntity<>(preferenceData, headers);
//
//            // Enviar la solicitud a Mercado Pago
//            ResponseEntity<Map> response = restTemplate.postForEntity(
//                    "https://api.mercadopago.com/checkout/preferences",
//                    request,
//                    Map.class
//            );
//
//            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
//                Map<String, Object> responseBody = response.getBody();
//                String initPoint = (String) responseBody.get("init_point");
//
//                // Retornar la URL de redirección de Mercado Pago
//                return ResponseEntity.ok(initPoint);
//            } else {
//                return ResponseEntity.status(response.getStatusCode()).body("Error al crear la preferencia de pago");
//            }
//
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error al crear la preferencia de pago: " + e.getMessage());
//        }
//    }
@PostMapping("/create-preference")
public ResponseEntity<String> createPreference(@RequestParam("amount") BigDecimal amount, @RequestParam("description") String description) {
    try {
        // Crear el item de la preferencia
        Map<String, Object> item = new HashMap<>();
        item.put("title", description);
        item.put("quantity", 1);
        item.put("unit_price", amount);

        // Crear la preferencia con URLs de redirección
        Map<String, Object> preferenceData = new HashMap<>();
        preferenceData.put("items", Collections.singletonList(item));

        // Agregar URLs de redirección
        Map<String, String> backUrls = new HashMap<>();
        backUrls.put("success", "https://ecommerce-angular-6bsx42ny5-furiac3ltas-projects.vercel.app/confirmacion-pago?status=success");
        backUrls.put("failure", "https://ecommerce-angular-6bsx42ny5-furiac3ltas-projects.vercel.app/confirmacion-pago?status=failure");
        backUrls.put("pending", "https://ecommerce-angular-6bsx42ny5-furiac3ltas-projects.vercel.app/confirmacion-pago?status=pending");
        preferenceData.put("back_urls", backUrls);

        // Configurar RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(preferenceData, headers);

        // Enviar la solicitud a Mercado Pago
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.mercadopago.com/checkout/preferences",
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            Map<String, Object> responseBody = response.getBody();
            String initPoint = (String) responseBody.get("init_point");

            // Retornar la URL de redirección de Mercado Pago
            return ResponseEntity.ok(initPoint);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Error al crear la preferencia de pago");
        }

    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error al crear la preferencia de pago: " + e.getMessage());
    }
}

}

