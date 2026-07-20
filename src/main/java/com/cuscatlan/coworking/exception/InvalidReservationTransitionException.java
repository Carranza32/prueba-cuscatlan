package com.cuscatlan.coworking.exception;

public class InvalidReservationTransitionException extends RuntimeException {
    public InvalidReservationTransitionException(String message) {
        super(message);
    }
}