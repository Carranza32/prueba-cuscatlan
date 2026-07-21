package com.cuscatlan.coworking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payment-gateway")
public record PaymentGatewayProperties(
        String baseUrl,
        long timeoutMs
) {
}