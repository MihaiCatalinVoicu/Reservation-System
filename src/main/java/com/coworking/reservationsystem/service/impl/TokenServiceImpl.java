package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.entity.BlacklistedToken;
import com.coworking.reservationsystem.model.entity.RefreshToken;
import com.coworking.reservationsystem.model.entity.User;
import com.coworking.reservationsystem.repository.BlacklistedTokenRepository;
import com.coworking.reservationsystem.repository.RefreshTokenRepository;
import com.coworking.reservationsystem.service.TokenService;
import com.coworking.reservationsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String userAgent, String ipAddress) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("tenantId", user.getTenant().getId());
        
        String token = jwtUtil.generateRefreshToken(user.getEmail(), claims);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7 days
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setRevoked(false);
        
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid refresh token"));
        
        if (refreshToken.isRevoked()) {
            throw new ValidationException("Refresh token has been revoked");
        }
        
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Refresh token has expired");
        }
        
        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.revokeToken(token);
        log.info("Revoked refresh token: {}", token);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
        log.info("Revoked all tokens for user: {}", userId);
    }

    @Override
    @Transactional
    public void blacklistToken(String token, Long userId, String reason) {
        // Extract expiration from JWT token
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // Default 1 hour
        try {
            var claims = jwtUtil.extractAllClaims(token);
            expiresAt = LocalDateTime.ofInstant(claims.getExpiration().toInstant(), java.time.ZoneId.systemDefault());
        } catch (Exception e) {
            log.warn("Could not extract expiration from token, using default: {}", e.getMessage());
        }
        
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setUserId(userId);
        blacklistedToken.setReason(reason);
        blacklistedToken.setBlacklistedAt(LocalDateTime.now());
        blacklistedToken.setExpiresAt(expiresAt);
        
        blacklistedTokenRepository.save(blacklistedToken);
        log.info("Blacklisted token for user: {}, reason: {}", userId, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        
        // Clean up expired refresh tokens
        refreshTokenRepository.deleteExpiredTokens(now);
        
        // Clean up expired blacklisted tokens
        blacklistedTokenRepository.deleteExpiredTokens(now);
        
        log.info("Cleaned up expired tokens at: {}", now);
    }
} 