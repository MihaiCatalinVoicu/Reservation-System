package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.Availability;
import com.coworking.reservationsystem.model.entity.Space;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AvailabilityDto(
        Long id,
        
        @NotNull(message = "Space ID is mandatory")
        Long spaceId,
        
        @NotNull(message = "Start time is mandatory")
        LocalDateTime startTime,
        
        @NotNull(message = "End time is mandatory")
        LocalDateTime endTime,
        
        LocalDateTime createdAt,
        
        Long tenantId
) {
    public static class Mapper {
        public static AvailabilityDto toDto(Availability availability) {
            return new AvailabilityDto(
                    availability.getId(),
                    availability.getSpace().getId(),
                    availability.getStartTime(),
                    availability.getEndTime(),
                    availability.getCreatedAt(),
                    availability.getTenant() != null ? availability.getTenant().getId() : null
            );
        }

        public static Availability toEntity(AvailabilityDto dto, Space space) {
            Availability availability = new Availability();
            availability.setSpace(space);
            availability.setStartTime(dto.startTime());
            availability.setEndTime(dto.endTime());
            availability.setCreatedAt(LocalDateTime.now());
            return availability;
        }
    }
}
