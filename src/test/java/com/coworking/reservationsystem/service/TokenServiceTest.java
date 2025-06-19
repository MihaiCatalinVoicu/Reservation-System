package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.entity.RefreshToken;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.model.entity.User;
import com.coworking.reservationsystem.repository.BlacklistedTokenRepository;
import com.coworking.reservationsystem.repository.RefreshTokenRepository;
import com.coworking.reservationsystem.service.impl.TokenServiceImpl;
import com.coworking.reservationsystem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        // Create tenant
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("test-tenant");
        tenant.setSubdomain("test");
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setTenant(tenant);
        testUser.setRoles(List.of("ROLE_USER"));

        testRefreshToken = new RefreshToken();
        testRefreshToken.setId(1L);
        testRefreshToken.setToken("test-refresh-token");
        testRefreshToken.setUser(testUser);
        testRefreshToken.setCreatedAt(LocalDateTime.now());
        testRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        testRefreshToken.setRevoked(false);
    }

    @Test
    void createRefreshToken_Success() {
        // Given
        when(jwtUtil.generateRefreshToken(any(), any())).thenReturn("new-refresh-token");
        when(refreshTokenRepository.save(any())).thenReturn(testRefreshToken);

        // When
        RefreshToken result = tokenService.createRefreshToken(testUser, "test-agent", "127.0.0.1");

        // Then
        assertNotNull(result);
        assertEquals(testRefreshToken.getToken(), result.getToken());
        assertEquals(testUser, result.getUser());
        assertFalse(result.isRevoked());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void verifyRefreshToken_Success() {
        // Given
        when(refreshTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(testRefreshToken));

        // When
        RefreshToken result = tokenService.verifyRefreshToken("valid-token");

        // Then
        assertNotNull(result);
        assertEquals(testRefreshToken, result);
    }

    @Test
    void verifyRefreshToken_InvalidToken_ThrowsException() {
        // Given
        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValidationException.class, () -> 
                tokenService.verifyRefreshToken("invalid-token"));
    }

    @Test
    void verifyRefreshToken_RevokedToken_ThrowsException() {
        // Given
        testRefreshToken.setRevoked(true);
        when(refreshTokenRepository.findByToken("revoked-token"))
                .thenReturn(Optional.of(testRefreshToken));

        // When & Then
        assertThrows(ValidationException.class, () -> 
                tokenService.verifyRefreshToken("revoked-token"));
    }

    @Test
    void verifyRefreshToken_ExpiredToken_ThrowsException() {
        // Given
        testRefreshToken.setExpiresAt(LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(testRefreshToken));

        // When & Then
        assertThrows(ValidationException.class, () -> 
                tokenService.verifyRefreshToken("expired-token"));
    }

    @Test
    void revokeRefreshToken_Success() {
        // Given
        doNothing().when(refreshTokenRepository).revokeToken("token-to-revoke");

        // When
        tokenService.revokeRefreshToken("token-to-revoke");

        // Then
        verify(refreshTokenRepository).revokeToken("token-to-revoke");
    }

    @Test
    void blacklistToken_Success() {
        // Given
        when(blacklistedTokenRepository.save(any())).thenReturn(null);

        // When
        tokenService.blacklistToken("token-to-blacklist", 1L, "LOGOUT");

        // Then
        verify(blacklistedTokenRepository).save(any());
    }

    @Test
    void isTokenBlacklisted_True() {
        // Given
        when(blacklistedTokenRepository.existsByToken("blacklisted-token"))
                .thenReturn(true);

        // When
        boolean result = tokenService.isTokenBlacklisted("blacklisted-token");

        // Then
        assertTrue(result);
    }

    @Test
    void isTokenBlacklisted_False() {
        // Given
        when(blacklistedTokenRepository.existsByToken("valid-token"))
                .thenReturn(false);

        // When
        boolean result = tokenService.isTokenBlacklisted("valid-token");

        // Then
        assertFalse(result);
    }
} 