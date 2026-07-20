package com.cuscatlan.coworking.dto.response;

import com.cuscatlan.coworking.domain.SpaceType;

import java.math.BigDecimal;
import java.time.Instant;

public record SpaceResponse(
        Long id,
        String name,
        SpaceType type,
        Integer capacity,
        String location,
        BigDecimal hourlyRate,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
