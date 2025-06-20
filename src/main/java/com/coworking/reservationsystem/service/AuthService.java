package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.LoginRequest;
import com.coworking.reservationsystem.model.dto.LoginResponse;
import com.coworking.reservationsystem.model.dto.RegisterRequest;
import com.coworking.reservationsystem.model.dto.UserDto;

public interface AuthService {
    
    /**
     * Authenticate user and return login response
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * Register a new user
     */
    UserDto register(RegisterRequest registerRequest);
} 