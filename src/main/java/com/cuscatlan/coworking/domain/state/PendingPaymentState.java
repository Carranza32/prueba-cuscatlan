package com.cuscatlan.coworking.domain.state;

import com.cuscatlan.coworking.domain.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class PendingPaymentState extends AbstractReservationState {

    @Override
    public ReservationStatus confirm() {
        return ReservationStatus.CONFIRMED;
    }

    @Override
    public ReservationStatus cancel() {
        return ReservationStatus.CANCELLED;
    }

    @Override
    public ReservationStatus complete() {
        return reject("completar");
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.PENDING_PAYMENT;
    }
}