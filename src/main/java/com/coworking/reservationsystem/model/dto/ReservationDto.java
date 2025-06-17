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

        @NotNull(message = "User ID is mandatory")
        Long userId,

        @NotNull(message = "Start time is mandatory")
        LocalDateTime startTime,

        @NotNull(message = "End time is mandatory")
        LocalDateTime endTime,

        @NotNull
        @Positive
        Double totalPrice,

        @NotNull
        Status status,

        LocalDateTime createdAt
) {
    public static class Mapper {
        public static ReservationDto toDto(Reservation reservation) {
            return new ReservationDto(
                    reservation.getId(),
                    reservation.getSpace().getId(),
                    reservation.getUser().getId(),
                    reservation.getStartTime(),
                    reservation.getEndTime(),
                    reservation.getTotalPrice(),
                    reservation.getStatus(),
                    reservation.getCreatedAt()
            );
        }

        public static Reservation toEntity(ReservationDto dto) {
            Reservation reservation = new Reservation();
            reservation.setStartTime(dto.startTime());
            reservation.setEndTime(dto.endTime());
            reservation.setTotalPrice(dto.totalPrice());
            reservation.setStatus(dto.status());
            reservation.setCreatedAt(LocalDateTime.now());
            return reservation;
        }
    }
}
