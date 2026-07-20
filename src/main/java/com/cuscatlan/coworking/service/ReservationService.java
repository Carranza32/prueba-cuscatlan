package com.cuscatlan.coworking.service;

import com.cuscatlan.coworking.domain.Reservation;
import com.cuscatlan.coworking.domain.ReservationStatus;
import com.cuscatlan.coworking.domain.Space;
import com.cuscatlan.coworking.domain.User;
import com.cuscatlan.coworking.domain.state.ReservationState;
import com.cuscatlan.coworking.domain.state.ReservationStateResolver;
import com.cuscatlan.coworking.dto.request.ReservationRequest;
import com.cuscatlan.coworking.dto.response.ReservationResponse;
import com.cuscatlan.coworking.exception.OverlappingReservationException;
import com.cuscatlan.coworking.exception.ResourceNotFoundException;
import com.cuscatlan.coworking.mapper.ReservationMapper;
import com.cuscatlan.coworking.repository.ReservationRepository;
import com.cuscatlan.coworking.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationStateResolver stateResolver;

    @Transactional
    public ReservationResponse create(ReservationRequest request, User currentUser){
        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        Space space = spaceRepository.findById(request.spaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado: " + request.spaceId()));

        List<Reservation> overlapping = reservationRepository.findOverlapping(
                space.getId(), request.startTime(), request.endTime(), ReservationStatus.CANCELLED
        );

        if (!overlapping.isEmpty()) {
            throw new OverlappingReservationException(
                    "El espacio ya tiene una reserva en ese horario"
            );
        }

        Reservation reservation = Reservation.builder()
                .space(space)
                .user(currentUser)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(ReservationStatus.PENDING)
                .build();

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findMine(User currentUser) {
        return reservationRepository.findByUserId(currentUser.getId())
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Transactional
    public void cancel(Long reservationId, User currentUser) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + reservationId));

        boolean isOwner = reservation.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("No puedes cancelar una reserva que no es tuya");
        }

        ReservationState currentState = stateResolver.resolve(reservation.getStatus());
        reservation.setStatus(currentState.cancel());
    }
}
