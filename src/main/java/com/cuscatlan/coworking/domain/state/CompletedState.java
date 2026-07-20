package com.cuscatlan.coworking.domain.state;

import com.cuscatlan.coworking.domain.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class CompletedState extends AbstractReservationState {

    @Override
    public ReservationStatus confirm() {
        return reject("confirmar");
    }

    @Override
    public ReservationStatus cancel() {
        return reject("cancelar una reserva ya completada");
    }

    @Override
    public ReservationStatus complete() {
        return reject("completar de nuevo");
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.COMPLETED;
    }
}