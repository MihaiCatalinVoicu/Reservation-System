package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.model.entity.Tenant;
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

@WebMvcTest(SpaceController.class)
class SpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpaceService spaceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tenant testTenant;
    private Space testSpace;
    private SpaceDto testSpaceDto;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setName("Test Hotel");
        testTenant.setSubdomain("test-hotel");

        testSpace = new Space();
        testSpace.setId(1L);
        testSpace.setName("Conference Room A");
        testSpace.setDescription("Large conference room with projector");
        testSpace.setCapacity(50);
        testSpace.setTenant(testTenant);

        testSpaceDto = new SpaceDto(
                1L,
                "Conference Room A",
                "Large conference room with projector",
                50,
                1L, // locationId
                100.0, // pricePerHour
                1L // tenantId
        );
    }

    @Test
    void createSpace_ValidSpace_ReturnsCreatedSpace() throws Exception {
        when(spaceService.createSpace(any(SpaceDto.class))).thenReturn(testSpaceDto);

        mockMvc.perform(post("/api/v1/spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSpaceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Conference Room A"))
                .andExpect(jsonPath("$.capacity").value(50))
                .andExpect(jsonPath("$.tenantId").value(1));

        verify(spaceService).createSpace(any(SpaceDto.class));
    }

    @Test
    void createSpace_InvalidSpace_ReturnsBadRequest() throws Exception {
        SpaceDto invalidSpace = new SpaceDto(
                null, "", "", 0, null, -1.0, null
        );

        mockMvc.perform(post("/api/v1/spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSpace)))
                .andExpect(status().isBadRequest());

        verify(spaceService, never()).createSpace(any());
    }

    @Test
    void getSpaceById_ExistingSpace_ReturnsSpace() throws Exception {
        when(spaceService.getSpaceById(1L)).thenReturn(testSpaceDto);

        mockMvc.perform(get("/api/v1/spaces/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Conference Room A"));

        verify(spaceService).getSpaceById(1L);
    }

    @Test
    void getSpaceById_NonExistentSpace_ReturnsNotFound() throws Exception {
        doThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Space not found")).when(spaceService).getSpaceById(999L);

        mockMvc.perform(get("/api/v1/spaces/999"))
                .andExpect(status().isNotFound());

        verify(spaceService).getSpaceById(999L);
    }

    @Test
    void getAllSpaces_ReturnsSpacesList() throws Exception {
        List<SpaceDto> spaces = Arrays.asList(testSpaceDto);
        when(spaceService.getAllSpaces()).thenReturn(spaces);

        mockMvc.perform(get("/api/v1/spaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Conference Room A"));

        verify(spaceService).getAllSpaces();
    }

    @Test
    void updateSpace_ValidSpace_ReturnsUpdatedSpace() throws Exception {
        when(spaceService.updateSpace(eq(1L), any(SpaceDto.class))).thenReturn(Optional.of(testSpaceDto));

        mockMvc.perform(put("/api/v1/spaces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSpaceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Conference Room A"));

        verify(spaceService).updateSpace(eq(1L), any(SpaceDto.class));
    }

    @Test
    void updateSpace_NonExistentSpace_ReturnsNotFound() throws Exception {
        when(spaceService.updateSpace(eq(999L), any(SpaceDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/spaces/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSpaceDto)))
                .andExpect(status().isNotFound());

        verify(spaceService).updateSpace(eq(999L), any(SpaceDto.class));
    }

    @Test
    void deleteSpace_ExistingSpace_ReturnsNoContent() throws Exception {
        doNothing().when(spaceService).deleteSpace(1L);

        mockMvc.perform(delete("/api/v1/spaces/1"))
                .andExpect(status().isNoContent());

        verify(spaceService).deleteSpace(1L);
    }

    @Test
    void deleteSpace_NonExistentSpace_ReturnsNotFound() throws Exception {
        doThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Space not found")).when(spaceService).deleteSpace(999L);

        mockMvc.perform(delete("/api/v1/spaces/999"))
                .andExpect(status().isNotFound());

        verify(spaceService).deleteSpace(999L);
    }

    @Test
    void getSpacesByLocation_ReturnsSpacesList() throws Exception {
        List<SpaceDto> spaces = Arrays.asList(testSpaceDto);
        when(spaceService.getSpacesByLocationId(1L)).thenReturn(spaces);

        mockMvc.perform(get("/api/v1/spaces/location/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Conference Room A"));

        verify(spaceService).getSpacesByLocationId(1L);
    }

    @Test
    void getSpacesByCapacity_ReturnsSpacesList() throws Exception {
        List<SpaceDto> spaces = Arrays.asList(testSpaceDto);
        when(spaceService.getSpacesByCapacity(50)).thenReturn(spaces);

        mockMvc.perform(get("/api/v1/spaces/capacity/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Conference Room A"));

        verify(spaceService).getSpacesByCapacity(50);
    }
} 