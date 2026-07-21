package com.cuscatlan.coworking.service;

import com.cuscatlan.coworking.domain.Reservation;
import com.cuscatlan.coworking.domain.ReservationStatus;
import com.cuscatlan.coworking.domain.Space;
import com.cuscatlan.coworking.dto.response.OccupancyResponse;
import com.cuscatlan.coworking.repository.ReservationRepository;
import com.cuscatlan.coworking.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SpaceRepository spaceRepository;
    private final ReservationRepository reservationRepository;

    @Cacheable(value = "occupancyReport", key = "#rangeStart + '_' + #rangeEnd")
    public List<OccupancyResponse> occupancyByRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        long totalRangeMinutes = ChronoUnit.MINUTES.between(rangeStart, rangeEnd);

        return spaceRepository.findAll().stream()
                .map(space -> buildOccupancy(space, rangeStart, rangeEnd, totalRangeMinutes))
                .toList();
    }

    private OccupancyResponse buildOccupancy(Space space, LocalDateTime rangeStart, LocalDateTime rangeEnd, long totalRangeMinutes) {
        List<Reservation> reservations = reservationRepository.findActiveInRange(
                space.getId(), rangeStart, rangeEnd, ReservationStatus.CANCELLED
        );

        long reservedMinutes = reservations.stream()
                .mapToLong(r -> {
                    LocalDateTime effectiveStart = r.getStartTime().isBefore(rangeStart) ? rangeStart : r.getStartTime();
                    LocalDateTime effectiveEnd = r.getEndTime().isAfter(rangeEnd) ? rangeEnd : r.getEndTime();
                    return ChronoUnit.MINUTES.between(effectiveStart, effectiveEnd);
                })
                .sum();

        BigDecimal percentage = totalRangeMinutes == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(reservedMinutes)
                .divide(BigDecimal.valueOf(totalRangeMinutes), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        return new OccupancyResponse(space.getId(), space.getName(), percentage);
    }
}