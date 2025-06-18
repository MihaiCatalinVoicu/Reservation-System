package com.coworking.reservationsystem.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordDto(
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?*()\\-_.,;:])[\\S]{8,}$",
                message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
        )
        String password
) {} 