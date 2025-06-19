package com.coworking.reservationsystem.model.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserDto user
) {} 