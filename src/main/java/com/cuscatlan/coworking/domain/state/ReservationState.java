package com.cuscatlan.coworking.domain.state;

import com.cuscatlan.coworking.domain.ReservationStatus;

public interface ReservationState {
    ReservationStatus confirm();

    ReservationStatus cancel();

    ReservationStatus complete();

    ReservationStatus status();
}
