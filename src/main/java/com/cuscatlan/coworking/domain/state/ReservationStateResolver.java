package com.cuscatlan.coworking.domain.state;

import com.cuscatlan.coworking.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReservationStateResolver {

    private final List<ReservationState> states;

    private Map<ReservationStatus, ReservationState> registry;

    private Map<ReservationStatus, ReservationState> registry() {
        if (registry == null) {
            registry = new EnumMap<>(ReservationStatus.class);
            states.forEach(s -> registry.put(s.status(), s));
        }
        return registry;
    }

    public ReservationState resolve(ReservationStatus status) {
        return registry().get(status);
    }
}