package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.Space;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SpaceDto(
        Long id,

        @NotBlank(message = "Name is mandatory")
        @Size(max = 100, message = "Name can not exceed 100 characters.")
        String name,

        @Size(max = 500, message = "Description can not exceed 500 characters.")
        String description,

        @NotNull(message = "Capacity is mandatory")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @NotNull(message = "Location ID is mandatory")
        Long locationId,

        @NotNull(message = "Price per hour is mandatory")
        @Min(value = 0, message = "Price per hour must be non-negative")
        Double pricePerHour,
        
        Long tenantId
) {
    public static class Mapper {
        public static SpaceDto toDto(Space space) {
            return new SpaceDto(
                    space.getId(),
                    space.getName(),
                    space.getDescription(),
                    space.getCapacity(),
                    space.getLocation().getId(),
                    space.getPricePerHour(),
                    space.getTenant() != null ? space.getTenant().getId() : null
            );
        }

        public static Space toEntity(SpaceDto dto) {
            Space space = new Space();
            space.setName(dto.name());
            space.setDescription(dto.description());
            space.setCapacity(dto.capacity());
            space.setPricePerHour(dto.pricePerHour());
            return space;
        }
    }
}
