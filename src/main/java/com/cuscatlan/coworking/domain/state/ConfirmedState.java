package com.cuscatlan.coworking.domain.state;

import com.cuscatlan.coworking.domain.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class ConfirmedState extends AbstractReservationState {

    @Override
    public ReservationStatus confirm() {
        return reject("confirmar de nuevo");
    }

    @Override
    public ReservationStatus cancel() {
        return ReservationStatus.CANCELLED;
    }

    @Override
    public ReservationStatus complete() {
        return ReservationStatus.COMPLETED;
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.CONFIRMED;
    }
}