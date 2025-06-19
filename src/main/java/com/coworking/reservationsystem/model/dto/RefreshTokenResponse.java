package com.coworking.reservationsystem.model.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {} 