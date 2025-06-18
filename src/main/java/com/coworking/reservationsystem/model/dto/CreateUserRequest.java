package com.coworking.reservationsystem.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotNull
    @Valid
    private UserDto user;
    
    @NotNull
    @Valid
    private PasswordDto password;
} 