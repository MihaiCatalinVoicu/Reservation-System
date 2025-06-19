package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.config.TestSecurityConfig;
import com.coworking.reservationsystem.model.dto.TableReservationDto;
import com.coworking.reservationsystem.model.entity.TableReservation;
import com.coworking.reservationsystem.service.TableReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableReservationController.class)
@ContextConfiguration(classes = {TableReservationController.class})
@Import(TestSecurityConfig.class)
class TableReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private TableReservationDto testReservationDto;
    private List<TableReservationDto> testReservations;

    @BeforeEach
    void setUp() {
        LocalDateTime requestedTime = LocalDateTime.now().plusHours(2);
        LocalDateTime estimatedArrivalTime = LocalDateTime.now().plusHours(2).plusMinutes(15);

        testReservationDto = new TableReservationDto(
                1L,
                1L,
                1L,
                4,
                requestedTime,
                estimatedArrivalTime,
                TableReservation.TableReservationStatus.PENDING,
                "Test requests",
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        testReservations = Arrays.asList(testReservationDto);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTableReservation_ValidReservation_ReturnsCreatedReservation() throws Exception {
        when(reservationService.createTableReservation(any(TableReservationDto.class))).thenReturn(testReservationDto);

        mockMvc.perform(post("/api/v1/table-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReservationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.numberOfPeople").value(4))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(reservationService).createTableReservation(any(TableReservationDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTableReservation_InvalidReservation_ReturnsBadRequest() throws Exception {
        TableReservationDto invalidReservationDto = new TableReservationDto(
                null,
                null,
                null,
                -1,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/v1/table-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReservationDto)))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).createTableReservation(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationById_ExistingReservation_ReturnsReservation() throws Exception {
        when(reservationService.getTableReservationById(1L, 1L)).thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(get("/api/v1/table-reservations/1")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numberOfPeople").value(4));

        verify(reservationService).getTableReservationById(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationById_NonExistentReservation_ReturnsNotFound() throws Exception {
        when(reservationService.getTableReservationById(999L, 1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/table-reservations/999")
                        .param("tenantId", "1"))
                .andExpect(status().isNotFound());

        verify(reservationService).getTableReservationById(999L, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTableReservations_ReturnsReservationsList() throws Exception {
        when(reservationService.getAllTableReservationsByTenant(1L)).thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getAllTableReservationsByTenant(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationsByCustomer_ReturnsReservationsList() throws Exception {
        when(reservationService.getTableReservationsByCustomer(1L, 1L)).thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations/customer/1")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getTableReservationsByCustomer(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationsByCustomer_WithPagination_ReturnsPage() throws Exception {
        Page<TableReservationDto> page = new PageImpl<>(testReservations);
        when(reservationService.getTableReservationsByCustomer(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/table-reservations/customer/1/page")
                        .param("tenantId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerId").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(reservationService).getTableReservationsByCustomer(eq(1L), eq(1L), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPendingTableReservations_ReturnsPendingReservations() throws Exception {
        when(reservationService.getPendingTableReservations(1L)).thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations/pending")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getPendingTableReservations(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void confirmTableReservation_ValidReservation_ReturnsConfirmedReservation() throws Exception {
        when(reservationService.confirmTableReservation(1L, 1L)).thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/confirm")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).confirmTableReservation(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectTableReservation_ValidReservation_ReturnsRejectedReservation() throws Exception {
        when(reservationService.rejectTableReservation(1L, 1L)).thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/reject")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).rejectTableReservation(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void cancelTableReservation_ValidReservation_ReturnsCancelledReservation() throws Exception {
        when(reservationService.cancelTableReservation(1L, 1L)).thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/cancel")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).cancelTableReservation(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void completeTableReservation_ValidReservation_ReturnsCompletedReservation() throws Exception {
        when(reservationService.completeTableReservation(1L, 1L)).thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/complete")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).completeTableReservation(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTableReservation_ExistingReservation_ReturnsNoContent() throws Exception {
        when(reservationService.deleteTableReservation(1L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/table-reservations/1")
                        .param("tenantId", "1"))
                .andExpect(status().isNoContent());

        verify(reservationService).deleteTableReservation(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTableReservation_NonExistentReservation_ReturnsNotFound() throws Exception {
        when(reservationService.deleteTableReservation(999L, 1L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/table-reservations/999")
                        .param("tenantId", "1"))
                .andExpect(status().isNotFound());

        verify(reservationService).deleteTableReservation(999L, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationsByDateRange_ReturnsReservationsList() throws Exception {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(reservationService.getTableReservationsByDateRange(startDate, endDate, 1L)).thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations/date-range")
                        .param("tenantId", "1")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getTableReservationsByDateRange(startDate, endDate, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationsByDateRange_InvalidDates_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/table-reservations/date-range")
                        .param("tenantId", "1")
                        .param("startDate", "invalid-date")
                        .param("endDate", "invalid-date"))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).getTableReservationsByDateRange(any(), any(), anyLong());
    }

    @Test
    @WithMockUser(roles = "USER")
    void checkOverlappingReservations_NoOverlap_ReturnsFalse() throws Exception {
        when(reservationService.hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);

        mockMvc.perform(get("/api/v1/table-reservations/overlapping")
                        .param("tableId", "1")
                        .param("startTime", LocalDateTime.now().plusHours(2).toString())
                        .param("endTime", LocalDateTime.now().plusHours(3).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(reservationService).hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void checkOverlappingReservations_HasOverlap_ReturnsTrue() throws Exception {
        when(reservationService.hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

        mockMvc.perform(get("/api/v1/table-reservations/overlapping")
                        .param("tableId", "1")
                        .param("startTime", LocalDateTime.now().plusHours(2).toString())
                        .param("endTime", LocalDateTime.now().plusHours(3).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(reservationService).hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationCount_ReturnsCount() throws Exception {
        when(reservationService.getTableReservationCountByTenant(1L)).thenReturn(10L);

        mockMvc.perform(get("/api/v1/table-reservations/count")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10));

        verify(reservationService).getTableReservationCountByTenant(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTableReservationCountByStatus_ReturnsCount() throws Exception {
        when(reservationService.getTableReservationCountByStatus(TableReservation.TableReservationStatus.PENDING, 1L)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/table-reservations/count/status/PENDING")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(reservationService).getTableReservationCountByStatus(TableReservation.TableReservationStatus.PENDING, 1L);
    }

    @Test
    void accessWithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/table-reservations")
                        .param("tenantId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void accessWithoutTenantId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/table-reservations"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTableReservation_InvalidNumberOfPeople_ReturnsBadRequest() throws Exception {
        TableReservationDto invalidReservationDto = new TableReservationDto(
                1L,
                1L,
                1L,
                0,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                TableReservation.TableReservationStatus.PENDING,
                "Test requests",
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/table-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReservationDto)))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).createTableReservation(any());
    }
} 