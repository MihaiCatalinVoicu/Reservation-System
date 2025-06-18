package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.config.TestSecurityConfig;
import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.ReservationDto;
import com.coworking.reservationsystem.model.dto.Status;
import com.coworking.reservationsystem.service.ReservationService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@Import(TestSecurityConfig.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservationDto testReservationDto;

    @BeforeEach
    void setUp() {
        testReservationDto = new ReservationDto(
                1L,
                1L, // spaceId
                1L, // userId
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                150.0, // totalPrice
                Status.CONFIRMED,
                LocalDateTime.now(), // createdAt
                1L // tenantId
        );
    }

    @Test
    void createReservation_ValidReservation_ReturnsCreatedReservation() throws Exception {
        when(reservationService.createReservation(any(ReservationDto.class))).thenReturn(testReservationDto);

        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.spaceId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.tenantId").value(1));

        verify(reservationService).createReservation(any(ReservationDto.class));
    }

    @Test
    void createReservation_InvalidReservation_ReturnsBadRequest() throws Exception {
        ReservationDto invalidReservation = new ReservationDto(
                null, null, null, null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReservation)))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).createReservation(any());
    }

    @Test
    void getReservationById_ExistingReservation_ReturnsReservation() throws Exception {
        when(reservationService.getReservationById(1L)).thenReturn(testReservationDto);

        mockMvc.perform(get("/api/v1/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(reservationService).getReservationById(1L);
    }

    @Test
    void getReservationById_NonExistentReservation_ReturnsNotFound() throws Exception {
        when(reservationService.getReservationById(999L))
                .thenThrow(new ResourceNotFoundException("Reservation not found"));

        mockMvc.perform(get("/api/v1/reservations/999"))
                .andExpect(status().isNotFound());

        verify(reservationService).getReservationById(999L);
    }

    @Test
    void getAllReservations_ReturnsReservationsList() throws Exception {
        List<ReservationDto> reservations = Arrays.asList(testReservationDto);
        when(reservationService.getAllReservations()).thenReturn(reservations);

        mockMvc.perform(get("/api/v1/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));

        verify(reservationService).getAllReservations();
    }

    @Test
    void updateReservation_ValidReservation_ReturnsUpdatedReservation() throws Exception {
        when(reservationService.updateReservation(eq(1L), any(ReservationDto.class)))
                .thenReturn(testReservationDto);

        mockMvc.perform(put("/api/v1/reservations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(reservationService).updateReservation(eq(1L), any(ReservationDto.class));
    }

    @Test
    void updateReservation_NonExistentReservation_ReturnsNotFound() throws Exception {
        when(reservationService.updateReservation(eq(999L), any(ReservationDto.class)))
                .thenThrow(new ResourceNotFoundException("Reservation not found"));

        mockMvc.perform(put("/api/v1/reservations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationDto)))
                .andExpect(status().isNotFound());

        verify(reservationService).updateReservation(eq(999L), any(ReservationDto.class));
    }

    @Test
    void deleteReservation_ExistingReservation_ReturnsNoContent() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/api/v1/reservations/1"))
                .andExpect(status().isNoContent());

        verify(reservationService).deleteReservation(1L);
    }

    @Test
    void deleteReservation_NonExistentReservation_ReturnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Reservation not found")).when(reservationService).deleteReservation(999L);

        mockMvc.perform(delete("/api/v1/reservations/999"))
                .andExpect(status().isNotFound());

        verify(reservationService).deleteReservation(999L);
    }

    @Test
    void getReservationsByUser_ReturnsUserReservations() throws Exception {
        List<ReservationDto> reservations = Arrays.asList(testReservationDto);
        when(reservationService.getReservationsByUserId(1L)).thenReturn(reservations);

        mockMvc.perform(get("/api/v1/reservations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(reservationService).getReservationsByUserId(1L);
    }

    @Test
    void getReservationsBySpace_ReturnsSpaceReservations() throws Exception {
        List<ReservationDto> reservations = Arrays.asList(testReservationDto);
        when(reservationService.getReservationsBySpaceId(1L)).thenReturn(reservations);

        mockMvc.perform(get("/api/v1/reservations/space/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].spaceId").value(1));

        verify(reservationService).getReservationsBySpaceId(1L);
    }
} 