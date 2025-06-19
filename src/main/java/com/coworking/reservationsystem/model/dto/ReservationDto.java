package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.Reservation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public record ReservationDto(
        Long id,

        @NotNull(message = "Space ID is mandatory")
        Long spaceId,

        @NotNull(message = "Customer ID is mandatory")
        Long customerId,

        @NotNull(message = "Created by user ID is mandatory")
        Long createdByUserId,

        @NotNull(message = "Start time is mandatory")
        LocalDateTime startTime,

        @NotNull(message = "End time is mandatory")
        LocalDateTime endTime,

        @NotNull
        @Positive
        Double totalPrice,

        @NotNull
        Status status,

        String notes,

        LocalDateTime createdAt,
        
        LocalDateTime updatedAt,
        
        Long tenantId
) {
    public static class Mapper {
        public static ReservationDto toDto(Reservation reservation) {
            return new ReservationDto(
                    reservation.getId(),
                    reservation.getSpace().getId(),
                    reservation.getCustomer().getId(),
                    reservation.getCreatedByUser().getId(),
                    reservation.getStartTime(),
                    reservation.getEndTime(),
                    reservation.getTotalPrice(),
                    reservation.getStatus(),
                    reservation.getNotes(),
                    reservation.getCreatedAt(),
                    reservation.getUpdatedAt(),
                    reservation.getTenant() != null ? reservation.getTenant().getId() : null
            );
        }

        public static Reservation toEntity(ReservationDto dto) {
            Reservation reservation = new Reservation();
            reservation.setStartTime(dto.startTime());
            reservation.setEndTime(dto.endTime());
            reservation.setTotalPrice(dto.totalPrice());
            reservation.setStatus(dto.status());
            reservation.setNotes(dto.notes());
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setUpdatedAt(LocalDateTime.now());
            return reservation;
        }
    }
}
