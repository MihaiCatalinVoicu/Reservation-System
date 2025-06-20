package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.AvailabilityDto;
import com.coworking.reservationsystem.service.AvailabilityService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvailabilityService availabilityService;

    @Autowired
    private ObjectMapper objectMapper;

    private AvailabilityDto testAvailabilityDto;

    @BeforeEach
    void setUp() {
        testAvailabilityDto = new AvailabilityDto(
                1L,
                1L, // spaceId
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now(), // createdAt
                1L // tenantId
        );
    }

    @Test
    void createAvailability_Valid_ReturnsCreated() throws Exception {
        when(availabilityService.createAvailability(any(AvailabilityDto.class))).thenReturn(testAvailabilityDto);

        mockMvc.perform(post("/api/v1/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAvailabilityDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.spaceId").value(1));

        verify(availabilityService).createAvailability(any(AvailabilityDto.class));
    }

    @Test
    void createAvailability_Invalid_ReturnsBadRequest() throws Exception {
        AvailabilityDto invalidDto = new AvailabilityDto(
                null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/v1/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(availabilityService, never()).createAvailability(any());
    }

    @Test
    void getAvailabilityById_Existing_ReturnsOk() throws Exception {
        when(availabilityService.getAvailabilityById(1L)).thenReturn(testAvailabilityDto);

        mockMvc.perform(get("/api/v1/availabilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(availabilityService).getAvailabilityById(1L);
    }

    @Test
    void getAllAvailabilities_ReturnsList() throws Exception {
        List<AvailabilityDto> availabilities = Arrays.asList(testAvailabilityDto);
        when(availabilityService.getAllAvailabilities()).thenReturn(availabilities);

        mockMvc.perform(get("/api/v1/availabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(availabilityService).getAllAvailabilities();
    }

    @Test
    void getAvailabilitiesBySpaceId_ReturnsList() throws Exception {
        List<AvailabilityDto> availabilities = Arrays.asList(testAvailabilityDto);
        when(availabilityService.getAvailabilitiesBySpaceId(1L)).thenReturn(availabilities);

        mockMvc.perform(get("/api/v1/availabilities/space/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].spaceId").value(1));

        verify(availabilityService).getAvailabilitiesBySpaceId(1L);
    }

    @Test
    void updateAvailability_Valid_ReturnsUpdated() throws Exception {
        when(availabilityService.updateAvailability(eq(1L), any(AvailabilityDto.class))).thenReturn(testAvailabilityDto);

        mockMvc.perform(put("/api/v1/availabilities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAvailabilityDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(availabilityService).updateAvailability(eq(1L), any(AvailabilityDto.class));
    }

    @Test
    void deleteAvailability_Existing_ReturnsNoContent() throws Exception {
        doNothing().when(availabilityService).deleteAvailability(1L);

        mockMvc.perform(delete("/api/v1/availabilities/1"))
                .andExpect(status().isNoContent());

        verify(availabilityService).deleteAvailability(1L);
    }
} 