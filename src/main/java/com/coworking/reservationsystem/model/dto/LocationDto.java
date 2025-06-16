package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationDto(
        Long id,

        @NotBlank(message = "Name is mandatory")
        @Size(max = 100, message = "Name of location can not exceed 100 characters.")
        String name,

        @NotBlank(message = "Address is mandatory")
        @Size(max = 255, message = "Name of the address can not exceed 255 characters.")
        String address,

        @NotBlank(message = "City is mandatory")
        @Size(max = 50, message = "Name of the city can not exceed 50 characters.")
        String city
) {
    public static class Mapper {
        public static LocationDto toDto(Location location) {
            return new LocationDto(
                    location.getId(),
                    location.getName(),
                    location.getAddress(),
                    location.getCity()
            );
        }

        public static Location toEntity(LocationDto dto) {
            Location location = new Location();
            location.setName(dto.name());
            location.setCity(dto.city());
            location.setAddress(dto.address());
            return location;
        }
    }
}
