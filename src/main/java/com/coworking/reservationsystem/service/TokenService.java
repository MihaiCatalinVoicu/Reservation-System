package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.entity.RefreshToken;
import com.coworking.reservationsystem.model.entity.User;

public interface TokenService {
    
    RefreshToken createRefreshToken(User user, String userAgent, String ipAddress);
    
    RefreshToken verifyRefreshToken(String token);
    
    void revokeRefreshToken(String token);
    
    void revokeAllUserTokens(Long userId);
    
    void blacklistToken(String token, Long userId, String reason);
    
    boolean isTokenBlacklisted(String token);
    
    void cleanupExpiredTokens();
} 