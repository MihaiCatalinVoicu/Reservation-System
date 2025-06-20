package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.TableReservationDto;
import com.coworking.reservationsystem.model.entity.TableReservation;
import com.coworking.reservationsystem.service.TableReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(TableReservationController.class)
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
        testReservationDto = new TableReservationDto(
                1L,
                1L,
                1L,
                4,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                TableReservation.TableReservationStatus.CONFIRMED,
                "Test reservation",
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        testReservations = Arrays.asList(testReservationDto);
    }

    @Test
    void createReservation_ValidReservation_ReturnsCreatedReservation() throws Exception {
        when(reservationService.createTableReservation(any(TableReservationDto.class))).thenReturn(testReservationDto);

        mockMvc.perform(post("/api/v1/table-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReservationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(reservationService).createTableReservation(any(TableReservationDto.class));
    }

    @Test
    void createReservation_InvalidReservation_ReturnsBadRequest() throws Exception {
        TableReservationDto invalidReservationDto = new TableReservationDto(
                1L,
                null, // Missing tableId
                null, // Missing customerId
                0, // Invalid number of people
                LocalDateTime.now().minusHours(1), // Past start time
                LocalDateTime.now().plusHours(1),
                TableReservation.TableReservationStatus.CONFIRMED,
                "Test reservation",
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

    @Test
    void getReservationById_ExistingReservation_ReturnsReservation() throws Exception {
        when(reservationService.getTableReservationById(1L, 1L)).thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(get("/api/v1/table-reservations/1")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tableId").value(1));

        verify(reservationService).getTableReservationById(1L, 1L);
    }

    @Test
    void getReservationById_NonExistentReservation_ReturnsNotFound() throws Exception {
        when(reservationService.getTableReservationById(999L, 1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/table-reservations/999")
                        .param("tenantId", "1"))
                .andExpect(status().isNotFound());

        verify(reservationService).getTableReservationById(999L, 1L);
    }

    @Test
    void getAllReservations_ReturnsReservationsList() throws Exception {
        when(reservationService.getAllTableReservationsByTenant(1L)).thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableId").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getAllTableReservationsByTenant(1L);
    }

    @Test
    void getReservationsByTable_ReturnsReservationsList() throws Exception {
        when(reservationService.getTableReservationsByTable(1L, 1L)).thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations/table/1")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableId").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getTableReservationsByTable(1L, 1L);
    }

    @Test
    void getReservationsByCustomer_ReturnsReservationsList() throws Exception {
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
    void getReservationsByStatus_ReturnsReservationsList() throws Exception {
        when(reservationService.getTableReservationsByStatus(TableReservation.TableReservationStatus.CONFIRMED, 1L))
                .thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations/status/CONFIRMED")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getTableReservationsByStatus(TableReservation.TableReservationStatus.CONFIRMED, 1L);
    }

    @Test
    void getReservationsByDateRange_ReturnsReservationsList() throws Exception {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        
        when(reservationService.getTableReservationsByDateRange(startDate, endDate, 1L))
                .thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations/date-range")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableId").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getTableReservationsByDateRange(startDate, endDate, 1L);
    }

    @Test
    void getPendingReservations_ReturnsPendingReservations() throws Exception {
        when(reservationService.getPendingTableReservations(1L)).thenReturn(testReservations);

        mockMvc.perform(get("/api/v1/table-reservations/pending")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableId").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService).getPendingTableReservations(1L);
    }

    @Test
    void updateReservation_ValidReservation_ReturnsUpdatedReservation() throws Exception {
        TableReservationDto updatedReservationDto = new TableReservationDto(
                1L,
                1L,
                1L,
                6,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(4),
                TableReservation.TableReservationStatus.CONFIRMED,
                "Updated reservation",
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(reservationService.updateTableReservation(eq(1L), any(TableReservationDto.class)))
                .thenReturn(Optional.of(updatedReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReservationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfPeople").value(6))
                .andExpect(jsonPath("$.specialRequests").value("Updated reservation"));

        verify(reservationService).updateTableReservation(eq(1L), any(TableReservationDto.class));
    }

    @Test
    void updateReservation_NonExistentReservation_ReturnsNotFound() throws Exception {
        when(reservationService.updateTableReservation(eq(999L), any(TableReservationDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/table-reservations/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReservationDto)))
                .andExpect(status().isNotFound());

        verify(reservationService).updateTableReservation(eq(999L), any(TableReservationDto.class));
    }

    @Test
    void confirmReservation_ValidReservation_ReturnsConfirmedReservation() throws Exception {
        when(reservationService.confirmTableReservation(1L, 1L))
                .thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/confirm")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).confirmTableReservation(1L, 1L);
    }

    @Test
    void rejectReservation_ValidReservation_ReturnsRejectedReservation() throws Exception {
        when(reservationService.rejectTableReservation(1L, 1L))
                .thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/reject")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).rejectTableReservation(1L, 1L);
    }

    @Test
    void cancelReservation_ValidReservation_ReturnsCancelledReservation() throws Exception {
        when(reservationService.cancelTableReservation(1L, 1L))
                .thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/cancel")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).cancelTableReservation(1L, 1L);
    }

    @Test
    void completeReservation_ValidReservation_ReturnsCompletedReservation() throws Exception {
        when(reservationService.completeTableReservation(1L, 1L))
                .thenReturn(Optional.of(testReservationDto));

        mockMvc.perform(put("/api/v1/table-reservations/1/complete")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(reservationService).completeTableReservation(1L, 1L);
    }

    @Test
    void deleteReservation_ExistingReservation_ReturnsNoContent() throws Exception {
        when(reservationService.deleteTableReservation(1L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/table-reservations/1")
                        .param("tenantId", "1"))
                .andExpect(status().isNoContent());

        verify(reservationService).deleteTableReservation(1L, 1L);
    }

    @Test
    void deleteReservation_NonExistentReservation_ReturnsNotFound() throws Exception {
        when(reservationService.deleteTableReservation(999L, 1L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/table-reservations/999")
                        .param("tenantId", "1"))
                .andExpect(status().isNotFound());

        verify(reservationService).deleteTableReservation(999L, 1L);
    }

    @Test
    void getReservationCount_ReturnsCount() throws Exception {
        when(reservationService.getTableReservationCountByTenant(1L)).thenReturn(10L);

        mockMvc.perform(get("/api/v1/table-reservations/count")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10));

        verify(reservationService).getTableReservationCountByTenant(1L);
    }

    @Test
    void getReservationCountByStatus_ReturnsCount() throws Exception {
        when(reservationService.getTableReservationCountByStatus(TableReservation.TableReservationStatus.CONFIRMED, 1L))
                .thenReturn(5L);

        mockMvc.perform(get("/api/v1/table-reservations/count/status/CONFIRMED")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(reservationService).getTableReservationCountByStatus(TableReservation.TableReservationStatus.CONFIRMED, 1L);
    }

    @Test
    void checkOverlappingReservations_NoOverlap_ReturnsFalse() throws Exception {
        when(reservationService.hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        mockMvc.perform(get("/api/v1/table-reservations/overlapping")
                        .param("tableId", "1")
                        .param("startTime", LocalDateTime.now().plusHours(1).toString())
                        .param("endTime", LocalDateTime.now().plusHours(3).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(reservationService).hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void checkOverlappingReservations_HasOverlap_ReturnsTrue() throws Exception {
        when(reservationService.hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        mockMvc.perform(get("/api/v1/table-reservations/overlapping")
                        .param("tableId", "1")
                        .param("startTime", LocalDateTime.now().plusHours(1).toString())
                        .param("endTime", LocalDateTime.now().plusHours(3).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(reservationService).hasOverlappingReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void accessWithoutAuthentication_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/table-reservations")
                        .param("tenantId", "1"))
                .andExpect(status().isOk()); // Security is disabled, so unauthenticated access returns 200 OK
    }

    @Test
    void accessWithoutTenantId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/table-reservations"))
                .andExpect(status().isBadRequest());
    }
} 