package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.model.dto.LoginRequest;
import com.coworking.reservationsystem.model.dto.LoginResponse;
import com.coworking.reservationsystem.model.dto.RegisterRequest;
import com.coworking.reservationsystem.model.dto.UserDto;
import com.coworking.reservationsystem.model.entity.User;
import com.coworking.reservationsystem.repository.UserRepository;
import com.coworking.reservationsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.email());
        
        try {
            // Trim email to remove leading/trailing spaces
            String trimmedEmail = loginRequest.email().trim();
            
            Optional<User> userOpt = userRepository.findByEmail(trimmedEmail);
            
            if (userOpt.isEmpty()) {
                log.warn("Login failed: User not found for email: {}", trimmedEmail);
                throw new RuntimeException("Invalid credentials");
            }
            
            User user = userOpt.get();
            
            // Simple password validation (plain text comparison since security is disabled)
            if (!user.getPassword().equals(loginRequest.password())) {
                log.warn("Login failed: Invalid password for email: {}", trimmedEmail);
                throw new RuntimeException("Invalid credentials");
            }
            
            log.info("Login successful for user: {}", user.getEmail());
            
            // Return dummy tokens since security is disabled
            return new LoginResponse(
                    "dummy-access-token-" + System.currentTimeMillis(),
                    "dummy-refresh-token-" + System.currentTimeMillis(),
                    UserDto.fromEntity(user)
            );
            
        } catch (Exception e) {
            log.error("Login error for email {}: {}", loginRequest.email(), e.getMessage());
            throw e;
        }
    }

    @Override
    public UserDto register(RegisterRequest registerRequest) {
        log.info("Registration attempt for email: {}", registerRequest.email());
        
        // Check if user already exists
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }
        
        // Create new user (password stored in plain text since security is disabled)
        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(registerRequest.password()); // Plain text storage
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setRoles(Arrays.asList("USER")); // Set default role
        user.setCreatedAt(LocalDateTime.now());
        
        // Set default tenant (you might want to adjust this based on your business logic)
        // For now, we'll need to handle tenant assignment differently
        
        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully: {}", savedUser.getEmail());
        
        return UserDto.fromEntity(savedUser);
    }
} 