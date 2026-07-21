package com.cuscatlan.coworking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simula un servicio externo de validacion de pago, inestable:
 * ~30% de fallos y latencia variable, para poder probar el circuit breaker.
 */
@RestController
@RequestMapping("/mock/payment")
public class MockPaymentController {

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(200, 1500));

        if (ThreadLocalRandom.current().nextInt(100) < 30) {
            throw new RuntimeException("Servicio de pago no disponible");
        }

        return ResponseEntity.ok(Map.of("status", "APPROVED"));
    }
}