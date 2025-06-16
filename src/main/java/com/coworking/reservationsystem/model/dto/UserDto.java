package com.coworking.reservationsystem.model.dto;

import com.coworking.reservationsystem.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UserDto(

        Long id,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "First name is mandatory")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        String lastName,

        LocalDateTime createdAt
) {

    public static class Mapper {

        public static UserDto toDto(User user) {

            return new UserDto(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getCreatedAt()
                    );
        }

        public static User toEntity(UserDto dto) {
            User user = new User();
            user.setId(dto.id);
            user.setEmail(dto.email);
            user.setFirstName(dto.firstName);
            user.setLastName(dto.lastName);

            return user;
        }
    }
}
