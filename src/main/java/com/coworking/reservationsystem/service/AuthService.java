package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.LoginRequest;
import com.coworking.reservationsystem.model.dto.LoginResponse;
import com.coworking.reservationsystem.model.dto.RefreshTokenRequest;
import com.coworking.reservationsystem.model.dto.RefreshTokenResponse;
import com.coworking.reservationsystem.model.dto.RegisterRequest;
import com.coworking.reservationsystem.model.dto.UserDto;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    UserDto register(RegisterRequest registerRequest);
    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void logout(String refreshToken);
} 