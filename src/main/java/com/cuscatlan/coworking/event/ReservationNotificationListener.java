package com.cuscatlan.coworking.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReservationNotificationListener {

    @Async
    @EventListener
    public void onReservationConfirmed(ReservationConfirmedEvent event) {
        // Simulacion del envio de correo: en un caso real aqui iria una
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info(
                "Correo de confirmacion enviado a {} para la reserva #{} en '{}'",
                event.userEmail(), event.reservationId(), event.spaceName()
        );
    }
}