package com.cuscatlan.coworking.client;

import com.cuscatlan.coworking.config.PaymentGatewayProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayClient {

    private final PaymentGatewayProperties properties;

    private RestClient restClient() {
        return RestClient.create(properties.baseUrl());
    }

    @CircuitBreaker(name = "paymentValidation", fallbackMethod = "fallback")
    public boolean validatePayment(Long reservationId) {
        restClient().get()
                .uri("/validate")
                .retrieve()
                .toBodilessEntity();
        return true;
    }

    private boolean fallback(Long reservationId, Throwable throwable) {
        log.warn(
                "Circuit breaker activo o servicio de pago fallo para la reserva #{}. Motivo: {}",
                reservationId, throwable.getMessage()
        );
        return false;
    }
}