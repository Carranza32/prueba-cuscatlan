package com.cuscatlan.coworking.repository;

import com.cuscatlan.coworking.domain.Reservation;
import com.cuscatlan.coworking.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
            SELECT r FROM Reservation r
            WHERE r.space.id = :spaceId
              AND r.status <> :cancelledStatus
              AND r.startTime < :endTime
              AND r.endTime > :startTime
            """)
    List<Reservation> findOverlapping(
            @Param("spaceId") Long spaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("cancelledStatus") ReservationStatus cancelledStatus
    );
    List<Reservation> findByUserId(Long userId);

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.space.id = :spaceId
          AND r.status <> :cancelledStatus
          AND r.startTime < :rangeEnd
          AND r.endTime > :rangeStart
        """)
    List<Reservation> findActiveInRange(
            @Param("spaceId") Long spaceId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("cancelledStatus") ReservationStatus cancelledStatus
    );
}
