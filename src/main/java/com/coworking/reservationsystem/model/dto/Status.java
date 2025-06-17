package com.coworking.reservationsystem.model.dto;

public enum Status {
    PENDING,    // Initial state when reservation is created
    CONFIRMED,  // Reservation has been confirmed
    CANCELLED,  // Reservation has been cancelled
    COMPLETED,  // Reservation has been completed
    EXPIRED     // Reservation has expired without confirmation
}
