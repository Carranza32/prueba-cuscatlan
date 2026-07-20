package com.cuscatlan.coworking.mapper;

import com.cuscatlan.coworking.domain.Reservation;
import com.cuscatlan.coworking.dto.response.ReservationResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getSpace().getId(),
                reservation.getSpace().getName(),
                reservation.getUser().getId(),
                reservation.getUser().getEmail(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}