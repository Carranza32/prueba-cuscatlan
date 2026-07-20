package com.cuscatlan.coworking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull(message = "El espacio es obligatorio")
        Long spaceId,

        @NotNull(message = "La hora de inicio es obligatoria")
        @Future(message = "La reserva debe ser en el futuro")
        LocalDateTime startTime,

        @NotNull(message = "La hora de fin es obligatoria")
        @Future(message = "La reserva debe ser en el futuro")
        LocalDateTime endTime
) {
}
