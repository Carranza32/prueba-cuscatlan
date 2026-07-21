package com.cuscatlan.coworking.service;

import com.cuscatlan.coworking.client.PaymentGatewayClient;
import com.cuscatlan.coworking.domain.*;
import com.cuscatlan.coworking.domain.state.*;
import com.cuscatlan.coworking.dto.request.ReservationRequest;
import com.cuscatlan.coworking.exception.OverlappingReservationException;
import com.cuscatlan.coworking.exception.InvalidReservationTransitionException;
import com.cuscatlan.coworking.mapper.ReservationMapper;
import com.cuscatlan.coworking.repository.ReservationRepository;
import com.cuscatlan.coworking.repository.SpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private SpaceRepository spaceRepository;
    @Mock private ReservationMapper reservationMapper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PaymentGatewayClient paymentGatewayClient;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationStateResolver stateResolver;

    private User user;
    private Space space;

    @BeforeEach
    void setUp() {
        stateResolver = new ReservationStateResolver(List.of(
                new PendingState(), new PendingPaymentState(),
                new ConfirmedState(), new CancelledState(), new CompletedState()
        ));
        reservationService = new ReservationService(
                reservationRepository, spaceRepository, reservationMapper, stateResolver, eventPublisher, paymentGatewayClient
        );

        user = User.builder().id(1L).email("user@test.com").role(Role.USER).build();
        space = Space.builder().id(1L).name("Sala A").capacity(4)
                .hourlyRate(BigDecimal.TEN).build();
    }

    @Test
    void create_lanzaExcepcion_siHaySolapamiento() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);
        ReservationRequest request = new ReservationRequest(space.getId(), start, end);

        when(spaceRepository.findById(space.getId())).thenReturn(Optional.of(space));
        when(reservationRepository.findOverlapping(space.getId(), start, end, ReservationStatus.CANCELLED))
                .thenReturn(List.of(mock(Reservation.class)));

        assertThatThrownBy(() -> reservationService.create(request, user))
                .isInstanceOf(OverlappingReservationException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_guardaLaReserva_siNoHaySolapamiento() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);
        ReservationRequest request = new ReservationRequest(space.getId(), start, end);

        when(spaceRepository.findById(space.getId())).thenReturn(Optional.of(space));
        when(reservationRepository.findOverlapping(space.getId(), start, end, ReservationStatus.CANCELLED))
                .thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.create(request, user);

        verify(reservationRepository).save(argThat(r ->
                r.getStatus() == ReservationStatus.PENDING && r.getUser().equals(user)
        ));
    }

    @Test
    void cancel_cambiaEstadoACancelled_siEsElDueno() {
        Reservation reservation = Reservation.builder()
                .id(10L).user(user).space(space).status(ReservationStatus.PENDING).build();

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        reservationService.cancel(10L, user);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void cancel_lanzaAccessDenied_siNoEsElDuenoNiAdmin() {
        User otroUsuario = User.builder().id(99L).role(Role.USER).build();
        Reservation reservation = Reservation.builder()
                .id(10L).user(user).space(space).status(ReservationStatus.PENDING).build();

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancel(10L, otroUsuario))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void cancel_lanzaExcepcion_siLaReservaYaEstaCompletada() {
        Reservation reservation = Reservation.builder()
                .id(10L).user(user).space(space).status(ReservationStatus.COMPLETED).build();

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancel(10L, user))
                .isInstanceOf(InvalidReservationTransitionException.class);
    }
}