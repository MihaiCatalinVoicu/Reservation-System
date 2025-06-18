package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.TenantDto;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.impl.TenantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private TenantDto validTenantDto;
    private Tenant validTenant;

    @BeforeEach
    void setUp() {
        validTenantDto = new TenantDto(
                null,
                "hotel-exemplu",
                "hotel-exemplu",
                "Hotel Exemplu",
                "Un hotel de lux în centrul orașului",
                "contact@hotel-exemplu.ro",
                "+40 21 123 4567",
                "Strada Exemplu nr. 123",
                "București",
                "România",
                "Europe/Bucharest",
                "https://example.com/logo.png",
                "#FF5733",
                "#33FF57",
                TenantDto.TenantStatus.PENDING,
                TenantDto.TenantPlan.PREMIUM,
                LocalDateTime.now(),
                null,
                50,
                200,
                5000,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        validTenant = new Tenant();
        validTenant.setId(1L);
        validTenant.setName("hotel-exemplu");
        validTenant.setSubdomain("hotel-exemplu");
        validTenant.setDisplayName("Hotel Exemplu");
        validTenant.setContactEmail("contact@hotel-exemplu.ro");
        validTenant.setStatus(Tenant.TenantStatus.PENDING);
        validTenant.setPlan(Tenant.TenantPlan.PREMIUM);
        validTenant.setMaxUsers(50);
        validTenant.setMaxSpaces(200);
        validTenant.setMaxReservationsPerMonth(5000);
    }

    @Test
    void createTenant_Success() {
        // Given
        when(tenantRepository.existsBySubdomain(validTenantDto.getSubdomain())).thenReturn(false);
        when(tenantRepository.existsByName(validTenantDto.getName())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(validTenant);

        // When
        TenantDto result = tenantService.createTenant(validTenantDto);

        // Then
        assertNotNull(result);
        assertEquals(validTenantDto.getName(), result.getName());
        assertEquals(validTenantDto.getSubdomain(), result.getSubdomain());
        assertEquals(TenantDto.TenantStatus.PENDING, result.getStatus());
        assertEquals(TenantDto.TenantPlan.PREMIUM, result.getPlan());
        assertEquals(50, result.getMaxUsers());
        assertEquals(200, result.getMaxSpaces());
        assertEquals(5000, result.getMaxReservationsPerMonth());

        verify(tenantRepository).existsBySubdomain(validTenantDto.getSubdomain());
        verify(tenantRepository).existsByName(validTenantDto.getName());
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    void createTenant_DuplicateSubdomain_ThrowsException() {
        // Given
        when(tenantRepository.existsBySubdomain(validTenantDto.getSubdomain())).thenReturn(true);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> tenantService.createTenant(validTenantDto));
        assertEquals("Subdomain already exists: " + validTenantDto.getSubdomain(), exception.getMessage());

        verify(tenantRepository).existsBySubdomain(validTenantDto.getSubdomain());
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test
    void createTenant_DuplicateName_ThrowsException() {
        // Given
        when(tenantRepository.existsBySubdomain(validTenantDto.getSubdomain())).thenReturn(false);
        when(tenantRepository.existsByName(validTenantDto.getName())).thenReturn(true);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> tenantService.createTenant(validTenantDto));
        assertEquals("Tenant name already exists: " + validTenantDto.getName(), exception.getMessage());

        verify(tenantRepository).existsBySubdomain(validTenantDto.getSubdomain());
        verify(tenantRepository).existsByName(validTenantDto.getName());
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test
    void createTenant_MissingRequiredFields_ThrowsException() {
        // Given
        TenantDto invalidTenantDto = new TenantDto(
                null,
                "", // empty name
                "", // empty subdomain
                "", // empty displayName
                null,
                "", // empty contactEmail
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                TenantDto.TenantStatus.PENDING,
                TenantDto.TenantPlan.BASIC,
                LocalDateTime.now(),
                null,
                10,
                50,
                1000,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> tenantService.createTenant(invalidTenantDto));
        assertTrue(exception.getMessage().contains("Tenant name is required"));
    }

    @Test
    void getTenantById_Success() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));

        // When
        TenantDto result = tenantService.getTenantById(1L);

        // Then
        assertNotNull(result);
        assertEquals(validTenant.getId(), result.getId());
        assertEquals(validTenant.getName(), result.getName());

        verify(tenantRepository).findById(1L);
    }

    @Test
    void getTenantById_NotFound_ThrowsException() {
        // Given
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> tenantService.getTenantById(999L));
        assertEquals("Tenant not found with id: 999", exception.getMessage());

        verify(tenantRepository).findById(999L);
    }

    @Test
    void getTenantBySubdomain_Success() {
        // Given
        when(tenantRepository.findBySubdomain("hotel-exemplu")).thenReturn(Optional.of(validTenant));

        // When
        TenantDto result = tenantService.getTenantBySubdomain("hotel-exemplu");

        // Then
        assertNotNull(result);
        assertEquals(validTenant.getSubdomain(), result.getSubdomain());

        verify(tenantRepository).findBySubdomain("hotel-exemplu");
    }

    @Test
    void getTenantBySubdomain_NotFound_ThrowsException() {
        // Given
        when(tenantRepository.findBySubdomain("inexistent")).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> tenantService.getTenantBySubdomain("inexistent"));
        assertEquals("Tenant not found with subdomain: inexistent", exception.getMessage());

        verify(tenantRepository).findBySubdomain("inexistent");
    }

    @Test
    void getAllTenants_Success() {
        // Given
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        tenant2.setName("hotel-exemplu-2");
        tenant2.setSubdomain("hotel-exemplu-2");
        tenant2.setDisplayName("Hotel Exemplu 2");
        tenant2.setContactEmail("contact@hotel-exemplu-2.ro");

        List<Tenant> tenants = Arrays.asList(validTenant, tenant2);
        when(tenantRepository.findAll()).thenReturn(tenants);

        // When
        List<TenantDto> result = tenantService.getAllTenants();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(validTenant.getName(), result.get(0).getName());
        assertEquals(tenant2.getName(), result.get(1).getName());

        verify(tenantRepository).findAll();
    }

    @Test
    void getTenantsByStatus_Success() {
        // Given
        List<Tenant> activeTenants = Arrays.asList(validTenant);
        when(tenantRepository.findByStatus(Tenant.TenantStatus.ACTIVE)).thenReturn(activeTenants);

        // When
        List<TenantDto> result = tenantService.getTenantsByStatus(TenantDto.TenantStatus.ACTIVE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(tenantRepository).findByStatus(Tenant.TenantStatus.ACTIVE);
    }

    @Test
    void getTenantsByPlan_Success() {
        // Given
        List<Tenant> premiumTenants = Arrays.asList(validTenant);
        when(tenantRepository.findByPlan(Tenant.TenantPlan.PREMIUM)).thenReturn(premiumTenants);

        // When
        List<TenantDto> result = tenantService.getTenantsByPlan(TenantDto.TenantPlan.PREMIUM);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(tenantRepository).findByPlan(Tenant.TenantPlan.PREMIUM);
    }

    @Test
    void updateTenant_Success() {
        // Given
        TenantDto updateDto = new TenantDto(
                1L,
                "hotel-exemplu-updated",
                "hotel-exemplu-updated",
                "Hotel Exemplu Updated",
                "Descriere actualizată",
                "contact@hotel-exemplu-updated.ro",
                "+40 21 123 4568",
                "Strada Exemplu nr. 124",
                "București",
                "România",
                "Europe/Bucharest",
                "https://example.com/logo-updated.png",
                "#FF5734",
                "#33FF58",
                TenantDto.TenantStatus.ACTIVE,
                TenantDto.TenantPlan.ENTERPRISE,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(12),
                200,
                1000,
                25000,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));
        when(tenantRepository.existsBySubdomain("hotel-exemplu-updated")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(validTenant);

        // When
        TenantDto result = tenantService.updateTenant(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(tenantRepository).findById(1L);
        verify(tenantRepository).existsBySubdomain("hotel-exemplu-updated");
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    void updateTenant_NotFound_ThrowsException() {
        // Given
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> tenantService.updateTenant(999L, validTenantDto));
        assertEquals("Tenant not found with id: 999", exception.getMessage());

        verify(tenantRepository).findById(999L);
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test
    void suspendTenant_Success() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(validTenant);

        // When
        tenantService.suspendTenant(1L);

        // Then
        verify(tenantRepository).findById(1L);
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    void activateTenant_Success() {
        // Given
        validTenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(validTenant);

        // When
        tenantService.activateTenant(1L);

        // Then
        verify(tenantRepository).findById(1L);
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    void deleteTenant_Success() {
        // Given
        when(tenantRepository.existsById(1L)).thenReturn(true);

        // When
        tenantService.deleteTenant(1L);

        // Then
        verify(tenantRepository).existsById(1L);
        verify(tenantRepository).deleteById(1L);
    }

    @Test
    void deleteTenant_NotFound_ThrowsException() {
        // Given
        when(tenantRepository.existsById(999L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> tenantService.deleteTenant(999L));
        assertEquals("Tenant not found with id: 999", exception.getMessage());

        verify(tenantRepository).existsById(999L);
        verify(tenantRepository, never()).deleteById(any());
    }

    @Test
    void isTenantActive_True() {
        // Given
        validTenant.setStatus(Tenant.TenantStatus.ACTIVE);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));

        // When
        boolean result = tenantService.isTenantActive(1L);

        // Then
        assertTrue(result);
        verify(tenantRepository).findById(1L);
    }

    @Test
    void isTenantActive_False() {
        // Given
        validTenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));

        // When
        boolean result = tenantService.isTenantActive(1L);

        // Then
        assertFalse(result);
        verify(tenantRepository).findById(1L);
    }

    @Test
    void isTenantActive_NotFound_ReturnsFalse() {
        // Given
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = tenantService.isTenantActive(999L);

        // Then
        assertFalse(result);
        verify(tenantRepository).findById(999L);
    }

    @Test
    void getActiveTenantsCount_Success() {
        // Given
        when(tenantRepository.countActiveTenants()).thenReturn(5L);

        // When
        long result = tenantService.getActiveTenantsCount();

        // Then
        assertEquals(5L, result);
        verify(tenantRepository).countActiveTenants();
    }

    @Test
    void getTenantsCountByPlan_Success() {
        // Given
        when(tenantRepository.countByPlan(Tenant.TenantPlan.PREMIUM)).thenReturn(3L);

        // When
        long result = tenantService.getTenantsCountByPlan(TenantDto.TenantPlan.PREMIUM);

        // Then
        assertEquals(3L, result);
        verify(tenantRepository).countByPlan(Tenant.TenantPlan.PREMIUM);
    }
} 