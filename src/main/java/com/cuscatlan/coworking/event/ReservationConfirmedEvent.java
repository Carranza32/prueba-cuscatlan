package com.cuscatlan.coworking.event;

public record ReservationConfirmedEvent(
        Long reservationId,
        String userEmail,
        String spaceName
) {
}
