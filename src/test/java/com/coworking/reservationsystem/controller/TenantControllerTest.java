package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.config.TestSecurityConfig;
import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.TenantDto;
import com.coworking.reservationsystem.service.TenantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
@Import(TestSecurityConfig.class)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @Autowired
    private ObjectMapper objectMapper;

    private TenantDto validTenantDto;
    private TenantDto createdTenantDto;

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

        createdTenantDto = new TenantDto(
                1L,
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
    }

    @Test
    void createTenant_Success() throws Exception {
        // Given
        when(tenantService.createTenant(any(TenantDto.class))).thenReturn(createdTenantDto);

        // When & Then
        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTenantDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("hotel-exemplu"))
                .andExpect(jsonPath("$.subdomain").value("hotel-exemplu"))
                .andExpect(jsonPath("$.displayName").value("Hotel Exemplu"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.plan").value("PREMIUM"));

        verify(tenantService).createTenant(any(TenantDto.class));
    }

    @Test
    void createTenant_InvalidData_ReturnsBadRequest() throws Exception {
        // Given
        TenantDto invalidTenantDto = new TenantDto(
                null,
                "", // empty name - @NotBlank violation
                "", // empty subdomain - @NotBlank violation
                "", // empty displayName
                null,
                "invalid-email", // invalid email - @Email violation
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, // null status - @NotNull violation
                null, // null plan - @NotNull violation
                LocalDateTime.now(),
                null,
                -10, // negative maxUsers - @Positive violation
                -50, // negative maxSpaces - @Positive violation
                -1000, // negative maxReservationsPerMonth - @Positive violation
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // When & Then
        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTenantDto)))
                .andExpect(status().isBadRequest());

        verify(tenantService, never()).createTenant(any(TenantDto.class));
    }

    @Test
    void getTenantById_Success() throws Exception {
        // Given
        when(tenantService.getTenantById(1L)).thenReturn(createdTenantDto);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("hotel-exemplu"))
                .andExpect(jsonPath("$.subdomain").value("hotel-exemplu"));

        verify(tenantService).getTenantById(1L);
    }

    @Test
    void getTenantById_NotFound_ReturnsNotFound() throws Exception {
        // Given
        when(tenantService.getTenantById(999L))
                .thenThrow(new ResourceNotFoundException("Tenant not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/999"))
                .andExpect(status().isNotFound());

        verify(tenantService).getTenantById(999L);
    }

    @Test
    void getTenantBySubdomain_Success() throws Exception {
        // Given
        when(tenantService.getTenantBySubdomain("hotel-exemplu")).thenReturn(createdTenantDto);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/subdomain/hotel-exemplu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.subdomain").value("hotel-exemplu"));

        verify(tenantService).getTenantBySubdomain("hotel-exemplu");
    }

    @Test
    void getAllTenants_Success() throws Exception {
        // Given
        TenantDto tenant2 = new TenantDto(
                2L,
                "hotel-exemplu-2",
                "hotel-exemplu-2",
                "Hotel Exemplu 2",
                "Al doilea hotel",
                "contact@hotel-exemplu-2.ro",
                "+40 21 123 4568",
                "Strada Exemplu nr. 124",
                "București",
                "România",
                "Europe/Bucharest",
                null,
                null,
                null,
                TenantDto.TenantStatus.ACTIVE,
                TenantDto.TenantPlan.BASIC,
                LocalDateTime.now(),
                null,
                10,
                50,
                1000,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<TenantDto> tenants = Arrays.asList(createdTenantDto, tenant2);
        when(tenantService.getAllTenants()).thenReturn(tenants);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("hotel-exemplu"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("hotel-exemplu-2"));

        verify(tenantService).getAllTenants();
    }

    @Test
    void getTenantsByStatus_Success() throws Exception {
        // Given
        List<TenantDto> activeTenants = Arrays.asList(createdTenantDto);
        when(tenantService.getTenantsByStatus(TenantDto.TenantStatus.ACTIVE)).thenReturn(activeTenants);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(tenantService).getTenantsByStatus(TenantDto.TenantStatus.ACTIVE);
    }

    @Test
    void getTenantsByPlan_Success() throws Exception {
        // Given
        List<TenantDto> premiumTenants = Arrays.asList(createdTenantDto);
        when(tenantService.getTenantsByPlan(TenantDto.TenantPlan.PREMIUM)).thenReturn(premiumTenants);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/plan/PREMIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(tenantService).getTenantsByPlan(TenantDto.TenantPlan.PREMIUM);
    }

    @Test
    void updateTenant_Success() throws Exception {
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

        when(tenantService.updateTenant(eq(1L), any(TenantDto.class))).thenReturn(updateDto);

        // When & Then
        mockMvc.perform(put("/api/v1/tenants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("hotel-exemplu-updated"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.plan").value("ENTERPRISE"));

        verify(tenantService).updateTenant(eq(1L), any(TenantDto.class));
    }

    @Test
    void deleteTenant_Success() throws Exception {
        // Given
        doNothing().when(tenantService).deleteTenant(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/tenants/1"))
                .andExpect(status().isNoContent());

        verify(tenantService).deleteTenant(1L);
    }

    @Test
    void suspendTenant_Success() throws Exception {
        // Given
        doNothing().when(tenantService).suspendTenant(1L);

        // When & Then
        mockMvc.perform(post("/api/v1/tenants/1/suspend"))
                .andExpect(status().isOk());

        verify(tenantService).suspendTenant(1L);
    }

    @Test
    void activateTenant_Success() throws Exception {
        // Given
        doNothing().when(tenantService).activateTenant(1L);

        // When & Then
        mockMvc.perform(post("/api/v1/tenants/1/activate"))
                .andExpect(status().isOk());

        verify(tenantService).activateTenant(1L);
    }

    @Test
    void getExpiredSubscriptions_Success() throws Exception {
        // Given
        List<TenantDto> expiredTenants = Arrays.asList(createdTenantDto);
        when(tenantService.getExpiredSubscriptions()).thenReturn(expiredTenants);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(tenantService).getExpiredSubscriptions();
    }

    @Test
    void getSubscriptionsExpiringBetween_Success() throws Exception {
        // Given
        List<TenantDto> expiringTenants = Arrays.asList(createdTenantDto);
        when(tenantService.getSubscriptionsExpiringBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expiringTenants);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/expiring")
                        .param("startDate", "2024-01-01T00:00:00")
                        .param("endDate", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(tenantService).getSubscriptionsExpiringBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void isTenantActive_True() throws Exception {
        // Given
        when(tenantService.isTenantActive(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/1/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(tenantService).isTenantActive(1L);
    }

    @Test
    void isTenantActive_False() throws Exception {
        // Given
        when(tenantService.isTenantActive(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/1/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(tenantService).isTenantActive(1L);
    }

    @Test
    void isTenantActiveBySubdomain_True() throws Exception {
        // Given
        when(tenantService.isTenantActive("hotel-exemplu")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/subdomain/hotel-exemplu/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(tenantService).isTenantActive("hotel-exemplu");
    }

    @Test
    void getActiveTenantsCount_Success() throws Exception {
        // Given
        when(tenantService.getActiveTenantsCount()).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/stats/active-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(tenantService).getActiveTenantsCount();
    }

    @Test
    void getTenantsCountByPlan_Success() throws Exception {
        // Given
        when(tenantService.getTenantsCountByPlan(TenantDto.TenantPlan.PREMIUM)).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/api/v1/tenants/stats/plan-count/PREMIUM"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(tenantService).getTenantsCountByPlan(TenantDto.TenantPlan.PREMIUM);
    }
} 