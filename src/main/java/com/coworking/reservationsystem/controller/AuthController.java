package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.LoginRequest;
import com.coworking.reservationsystem.model.dto.LoginResponse;
import com.coworking.reservationsystem.model.dto.RegisterRequest;
import com.coworking.reservationsystem.model.dto.UserDto;
import com.coworking.reservationsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for email: {}", loginRequest.email());
        
        try {
            LoginResponse response = authService.login(loginRequest);
            log.info("Login successful for user: {}", loginRequest.email());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for email {}: {}", loginRequest.email(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for email: {}", registerRequest.email());
        
        try {
            UserDto user = authService.register(registerRequest);
            log.info("Registration successful for user: {}", registerRequest.email());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Registration failed for email {}: {}", registerRequest.email(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth controller is working!");
    }
} 