package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.*;
import com.coworking.reservationsystem.model.entity.RefreshToken;
import com.coworking.reservationsystem.model.entity.User;
import com.coworking.reservationsystem.repository.UserRepository;
import com.coworking.reservationsystem.service.AuthService;
import com.coworking.reservationsystem.service.TokenService;
import com.coworking.reservationsystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final HttpServletRequest request;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("tenantId", user.getTenant().getId());
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), claims);
        
        // Create persistent refresh token
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress();
        RefreshToken refreshTokenEntity = tokenService.createRefreshToken(user, userAgent, ipAddress);
        
        return new LoginResponse(accessToken, refreshTokenEntity.getToken(), UserDto.fromEntity(user));
    }

    @Override
    public UserDto register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ValidationException("Email already in use");
        }
        User user = new User();
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRoles(Collections.singletonList("CLIENT"));
        user.setCreatedAt(java.time.LocalDateTime.now());
        // Set tenant (should be fetched from TenantRepository, simplified here)
        user.setTenant(null); // TODO: set actual tenant
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        
        // Verify refresh token from persistent storage
        RefreshToken refreshTokenEntity = tokenService.verifyRefreshToken(refreshToken);
        User user = refreshTokenEntity.getUser();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("tenantId", user.getTenant().getId());
        
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), claims);
        
        // Create new refresh token and revoke old one
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress();
        RefreshToken newRefreshTokenEntity = tokenService.createRefreshToken(user, userAgent, ipAddress);
        tokenService.revokeRefreshToken(refreshToken);
        
        return new RefreshTokenResponse(newAccessToken, newRefreshTokenEntity.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        // Revoke refresh token
        tokenService.revokeRefreshToken(refreshToken);
        
        // Blacklist the current access token if available
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(accessToken);
                User user = userRepository.findByEmail(username)
                        .orElse(null);
                if (user != null) {
                    tokenService.blacklistToken(accessToken, user.getId(), "LOGOUT");
                }
            } catch (Exception e) {
                // Token might be invalid, ignore
            }
        }
    }
    
    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
} 