package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.RestaurantTable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record RestaurantTableDto(
        Long id,

        @NotNull(message = "Table name is mandatory")
        @Size(max = 100, message = "Table name cannot exceed 100 characters")
        String name,

        @NotNull(message = "Number of seats is mandatory")
        @Min(value = 1, message = "Number of seats must be at least 1")
        Integer numberOfSeats,

        @NotNull(message = "Table status is mandatory")
        RestaurantTable.TableStatus status,

        @NotNull(message = "Space ID is mandatory")
        Long spaceId,

        @NotNull(message = "Tenant ID is mandatory")
        Long tenantId,

        String notes,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
    public static class Mapper {
        public static RestaurantTableDto toDto(RestaurantTable table) {
            return new RestaurantTableDto(
                    table.getId(),
                    table.getName(),
                    table.getNumberOfSeats(),
                    table.getStatus(),
                    table.getSpace().getId(),
                    table.getTenant().getId(),
                    table.getNotes(),
                    table.getCreatedAt(),
                    table.getUpdatedAt()
            );
        }

        public static RestaurantTable toEntity(RestaurantTableDto dto) {
            RestaurantTable table = new RestaurantTable();
            table.setName(dto.name());
            table.setNumberOfSeats(dto.numberOfSeats());
            table.setStatus(dto.status());
            table.setNotes(dto.notes());
            return table;
        }
    }
} 