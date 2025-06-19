package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.TableReservation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TableReservationDto(
        Long id,

        @NotNull(message = "Table ID is mandatory")
        Long tableId,

        @NotNull(message = "Customer ID is mandatory")
        Long customerId,

        @NotNull(message = "Number of people is mandatory")
        @Min(value = 1, message = "Number of people must be at least 1")
        Integer numberOfPeople,

        @NotNull(message = "Requested time is mandatory")
        LocalDateTime requestedTime,

        @NotNull(message = "Estimated arrival time is mandatory")
        LocalDateTime estimatedArrivalTime,

        @NotNull(message = "Reservation status is mandatory")
        TableReservation.TableReservationStatus status,

        String specialRequests,

        @NotNull(message = "Tenant ID is mandatory")
        Long tenantId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
    public static class Mapper {
        public static TableReservationDto toDto(TableReservation reservation) {
            return new TableReservationDto(
                    reservation.getId(),
                    reservation.getTable().getId(),
                    reservation.getCustomer().getId(),
                    reservation.getNumberOfPeople(),
                    reservation.getRequestedTime(),
                    reservation.getEstimatedArrivalTime(),
                    reservation.getStatus(),
                    reservation.getSpecialRequests(),
                    reservation.getTenant().getId(),
                    reservation.getCreatedAt(),
                    reservation.getUpdatedAt()
            );
        }

        public static TableReservation toEntity(TableReservationDto dto) {
            TableReservation reservation = new TableReservation();
            reservation.setNumberOfPeople(dto.numberOfPeople());
            reservation.setRequestedTime(dto.requestedTime());
            reservation.setEstimatedArrivalTime(dto.estimatedArrivalTime());
            reservation.setStatus(dto.status());
            reservation.setSpecialRequests(dto.specialRequests());
            return reservation;
        }
    }
} 