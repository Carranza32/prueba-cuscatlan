package com.cuscatlan.coworking.repository;

import com.cuscatlan.coworking.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
