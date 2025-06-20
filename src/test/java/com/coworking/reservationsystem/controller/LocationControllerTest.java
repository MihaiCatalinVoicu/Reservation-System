package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.LocationDto;
import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.service.LocationService;
import com.coworking.reservationsystem.service.SpaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;
    @MockBean
    private SpaceService spaceService;
    @Autowired
    private ObjectMapper objectMapper;

    private LocationDto validLocationDto;
    private LocationDto createdLocationDto;
    private SpaceDto spaceDto;

    @BeforeEach
    void setUp() {
        validLocationDto = new LocationDto(
                null,
                "Sala Mare",
                "Strada Exemplu 1",
                "București",
                1L
        );
        createdLocationDto = new LocationDto(
                1L,
                "Sala Mare",
                "Strada Exemplu 1",
                "București",
                1L
        );
        spaceDto = new SpaceDto(
                1L,
                "Birou 1",
                "Descriere birou",
                10,
                1L,
                100.0,
                1L
        );
    }

    @Test
    void createLocation_Success() throws Exception {
        when(locationService.createLocation(any(LocationDto.class))).thenReturn(createdLocationDto);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLocationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sala Mare"))
                .andExpect(jsonPath("$.tenantId").value(1));

        verify(locationService).createLocation(any(LocationDto.class));
    }

    @Test
    void getLocationById_Success() throws Exception {
        when(locationService.getLocationById(1L)).thenReturn(createdLocationDto);

        mockMvc.perform(get("/api/v1/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sala Mare"));

        verify(locationService).getLocationById(1L);
    }

    @Test
    void getAllLocations_Success() throws Exception {
        List<LocationDto> locations = Arrays.asList(createdLocationDto);
        when(locationService.getAllLocations()).thenReturn(locations);

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(locationService).getAllLocations();
    }

    @Test
    void getSpacesByLocationId_Success() throws Exception {
        List<SpaceDto> spaces = Arrays.asList(spaceDto);
        when(spaceService.getSpacesByLocationId(1L)).thenReturn(spaces);

        mockMvc.perform(get("/api/v1/locations/1/spaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(spaceService).getSpacesByLocationId(1L);
    }

    @Test
    void updateLocation_Success() throws Exception {
        // Given
        LocationDto updateDto = new LocationDto(
                1L,
                "Sala Mare Actualizată",
                "Strada Exemplu 2",
                "București",
                1L
        );

        when(locationService.updateLocation(eq(1L), any(LocationDto.class))).thenReturn(updateDto);

        // When & Then
        mockMvc.perform(put("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sala Mare Actualizată"))
                .andExpect(jsonPath("$.address").value("Strada Exemplu 2"));

        verify(locationService).updateLocation(eq(1L), any(LocationDto.class));
    }

    @Test
    void updateLocation_NonExistentLocation_ReturnsNotFound() throws Exception {
        doThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Location not found")).when(locationService).updateLocation(eq(999L), any(LocationDto.class));

        mockMvc.perform(put("/api/v1/locations/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LocationDto(999L, "Sala Mare Actualizat?", "Strada Exemplu 2", "Bucure?ti", 1L)))
                )
                .andExpect(status().isNotFound());

        verify(locationService).updateLocation(eq(999L), any(LocationDto.class));
    }

    @Test
    void deleteLocation_ExistingLocation_ReturnsNoContent() throws Exception {
        doNothing().when(locationService).deleteLocation(1L);

        mockMvc.perform(delete("/api/v1/locations/1"))
                .andExpect(status().isNoContent());

        verify(locationService).deleteLocation(1L);
    }

    @Test
    void deleteLocation_NonExistentLocation_ReturnsNotFound() throws Exception {
        doThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Location not found")).when(locationService).deleteLocation(999L);

        mockMvc.perform(delete("/api/v1/locations/999"))
                .andExpect(status().isNotFound());

        verify(locationService).deleteLocation(999L);
    }

    @Test
    void createLocation_InvalidData_ReturnsBadRequest() throws Exception {
        // Given
        LocationDto invalidLocationDto = new LocationDto(
                null,
                "", // empty name - @NotBlank violation
                "", // empty address - @NotBlank violation
                "", // empty city - @NotBlank violation
                1L
        );

        // When & Then
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLocationDto)))
                .andExpect(status().isBadRequest());

        verify(locationService, never()).createLocation(any(LocationDto.class));
    }

    @Test
    void updateLocation_InvalidData_ReturnsBadRequest() throws Exception {
        // Given
        LocationDto invalidLocationDto = new LocationDto(
                1L,
                "", // empty name - @NotBlank violation
                "", // empty address - @NotBlank violation
                "", // empty city - @NotBlank violation
                1L
        );

        // When & Then
        mockMvc.perform(put("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLocationDto)))
                .andExpect(status().isBadRequest());

        verify(locationService, never()).updateLocation(any(), any(LocationDto.class));
    }
} 