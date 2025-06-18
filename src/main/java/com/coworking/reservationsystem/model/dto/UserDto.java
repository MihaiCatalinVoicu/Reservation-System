package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(
        Long id,
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "First name is mandatory")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,
        @NotBlank(message = "Last name is mandatory")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,
        LocalDateTime createdAt,
        List<String> roles
) {
    public static class Mapper {
        public static UserDto toDto(User user) {
            return new UserDto(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getCreatedAt(),
                    user.getRoles()
            );
        }

        public static User toEntity(UserDto dto) {
            User user = new User();
            user.setEmail(dto.email());
            user.setFirstName(dto.firstName());
            user.setLastName(dto.lastName());
            user.setRoles(dto.roles());
            return user;
        }
    }
}
