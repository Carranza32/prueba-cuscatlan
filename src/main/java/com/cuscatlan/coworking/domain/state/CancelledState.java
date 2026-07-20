package com.cuscatlan.coworking.domain.state;

import com.cuscatlan.coworking.domain.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class CancelledState extends AbstractReservationState {

    @Override
    public ReservationStatus confirm() {
        return reject("confirmar");
    }

    @Override
    public ReservationStatus cancel() {
        return reject("cancelar de nuevo");
    }

    @Override
    public ReservationStatus complete() {
        return reject("completar");
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.CANCELLED;
    }
}