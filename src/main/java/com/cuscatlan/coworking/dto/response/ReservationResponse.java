package com.cuscatlan.coworking.dto.response;

import com.cuscatlan.coworking.domain.ReservationStatus;

import java.time.Instant;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long spaceId,
        String spaceName,
        Long userId,
        String userEmail,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status,
        Instant createdAt
) {
}
