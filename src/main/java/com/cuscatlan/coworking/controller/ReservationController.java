package com.cuscatlan.coworking.controller;

import com.cuscatlan.coworking.domain.User;
import com.cuscatlan.coworking.dto.request.ReservationRequest;
import com.cuscatlan.coworking.dto.response.ReservationResponse;
import com.cuscatlan.coworking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request, currentUser));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ReservationResponse>> findMine(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(reservationService.findMine(currentUser));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        reservationService.cancel(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
