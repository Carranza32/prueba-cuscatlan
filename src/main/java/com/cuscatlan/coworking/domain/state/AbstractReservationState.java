package com.cuscatlan.coworking.domain.state;

import com.cuscatlan.coworking.domain.ReservationStatus;
import com.cuscatlan.coworking.exception.InvalidReservationTransitionException;

abstract class AbstractReservationState implements ReservationState{
    protected ReservationStatus reject(String action) {
        throw new InvalidReservationTransitionException(
                "No se puede " + action + " una reserva en estado " + status()
        );
    }
}
