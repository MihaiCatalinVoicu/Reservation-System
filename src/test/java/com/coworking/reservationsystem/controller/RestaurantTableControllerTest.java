package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.RestaurantTableDto;
import com.coworking.reservationsystem.model.entity.RestaurantTable;
import com.coworking.reservationsystem.service.RestaurantTableService;
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

@WebMvcTest(RestaurantTableController.class)
class RestaurantTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantTableService tableService;

    @Autowired
    private ObjectMapper objectMapper;

    private RestaurantTableDto testTableDto;
    private List<RestaurantTableDto> testTables;

    @BeforeEach
    void setUp() {
        testTableDto = new RestaurantTableDto(
                1L,
                "Test Table",
                4,
                RestaurantTable.TableStatus.AVAILABLE,
                1L,
                1L,
                "Test notes",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        testTables = Arrays.asList(testTableDto);
    }

    @Test
    void createTable_ValidTable_ReturnsCreatedTable() throws Exception {
        when(tableService.createTable(any(RestaurantTableDto.class))).thenReturn(testTableDto);

        mockMvc.perform(post("/api/v1/restaurant-tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTableDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Table"))
                .andExpect(jsonPath("$.numberOfSeats").value(4))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        verify(tableService).createTable(any(RestaurantTableDto.class));
    }

    @Test
    void createTable_InvalidTable_ReturnsBadRequest() throws Exception {
        RestaurantTableDto invalidTableDto = new RestaurantTableDto(
                1L,
                "Test Table",
                -1, // Invalid number of seats
                RestaurantTable.TableStatus.AVAILABLE,
                null, // Missing spaceId
                null, // Missing tenantId
                "Test notes",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/restaurant-tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTableDto)))
                .andExpect(status().isBadRequest());

        verify(tableService, never()).createTable(any());
    }

    @Test
    void getTableById_ExistingTable_ReturnsTable() throws Exception {
        when(tableService.getTableById(1L)).thenReturn(testTableDto);

        mockMvc.perform(get("/api/v1/restaurant-tables/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Table"));

        verify(tableService).getTableById(1L);
    }

    @Test
    void getTableById_NonExistentTable_ReturnsNotFound() throws Exception {
        when(tableService.getTableById(999L)).thenThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Table not found"));

        mockMvc.perform(get("/api/v1/restaurant-tables/999"))
                .andExpect(status().isNotFound());

        verify(tableService).getTableById(999L);
    }

    @Test
    void getAllTables_ReturnsTablesList() throws Exception {
        when(tableService.getAllTables()).thenReturn(testTables);

        mockMvc.perform(get("/api/v1/restaurant-tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Table"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(tableService).getAllTables();
    }

    @Test
    void getTablesBySpace_ReturnsTablesList() throws Exception {
        when(tableService.getTablesBySpace(1L)).thenReturn(testTables);

        mockMvc.perform(get("/api/v1/restaurant-tables/space/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].spaceId").value(1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(tableService).getTablesBySpace(1L);
    }

    @Test
    void getTablesByStatus_ReturnsTablesList() throws Exception {
        when(tableService.getTablesByStatus(RestaurantTable.TableStatus.AVAILABLE)).thenReturn(testTables);

        mockMvc.perform(get("/api/v1/restaurant-tables/status/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(tableService).getTablesByStatus(RestaurantTable.TableStatus.AVAILABLE);
    }

    @Test
    void getAvailableTables_ReturnsAvailableTables() throws Exception {
        when(tableService.getAvailableTables()).thenReturn(testTables);

        mockMvc.perform(get("/api/v1/restaurant-tables/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(tableService).getAvailableTables();
    }

    @Test
    void getAvailableTablesByMinSeats_ReturnsTablesWithMinSeats() throws Exception {
        when(tableService.getAvailableTablesByMinSeats(4)).thenReturn(testTables);

        mockMvc.perform(get("/api/v1/restaurant-tables/available/min-seats/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numberOfSeats").value(4))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(tableService).getAvailableTablesByMinSeats(4);
    }

    @Test
    void updateTable_ValidTable_ReturnsUpdatedTable() throws Exception {
        RestaurantTableDto updatedTableDto = new RestaurantTableDto(
                1L,
                "Updated Table",
                6,
                RestaurantTable.TableStatus.OCCUPIED,
                1L,
                1L,
                "Updated notes",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(tableService.updateTable(eq(1L), any(RestaurantTableDto.class))).thenReturn(updatedTableDto);

        mockMvc.perform(put("/api/v1/restaurant-tables/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTableDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Table"))
                .andExpect(jsonPath("$.numberOfSeats").value(6))
                .andExpect(jsonPath("$.status").value("OCCUPIED"));

        verify(tableService).updateTable(eq(1L), any(RestaurantTableDto.class));
    }

    @Test
    void updateTable_NonExistentTable_ReturnsNotFound() throws Exception {
        when(tableService.updateTable(eq(999L), any(RestaurantTableDto.class)))
            .thenThrow(new com.coworking.reservationsystem.exception.ResourceNotFoundException("Table not found"));

        mockMvc.perform(put("/api/v1/restaurant-tables/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTableDto)))
                .andExpect(status().isNotFound());

        verify(tableService).updateTable(eq(999L), any(RestaurantTableDto.class));
    }

    @Test
    void updateTableStatus_ValidStatus_ReturnsUpdatedTable() throws Exception {
        when(tableService.updateTableStatus(1L, RestaurantTable.TableStatus.OCCUPIED))
                .thenReturn(testTableDto);

        mockMvc.perform(put("/api/v1/restaurant-tables/1/status")
                        .param("status", "OCCUPIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(tableService).updateTableStatus(1L, RestaurantTable.TableStatus.OCCUPIED);
    }

    @Test
    void updateTableStatus_InvalidStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/v1/restaurant-tables/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());

        verify(tableService, never()).updateTableStatus(anyLong(), any());
    }

    @Test
    void deleteTable_ExistingTable_ReturnsNoContent() throws Exception {
        doNothing().when(tableService).deleteTable(1L);

        mockMvc.perform(delete("/api/v1/restaurant-tables/1"))
                .andExpect(status().isNoContent());

        verify(tableService).deleteTable(1L);
    }

    @Test
    void deleteTable_NonExistentTable_ReturnsNoContent() throws Exception {
        doNothing().when(tableService).deleteTable(999L);

        mockMvc.perform(delete("/api/v1/restaurant-tables/999"))
                .andExpect(status().isNoContent());

        verify(tableService).deleteTable(999L);
    }

    @Test
    void getTableCount_ReturnsCount() throws Exception {
        when(tableService.getTableCountByTenant(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/restaurant-tables/count")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(tableService).getTableCountByTenant(1L);
    }

    @Test
    void getTableCountByStatus_ReturnsCount() throws Exception {
        when(tableService.getTableCountByStatus(RestaurantTable.TableStatus.AVAILABLE, 1L)).thenReturn(3L);

        mockMvc.perform(get("/api/v1/restaurant-tables/count/status/AVAILABLE")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));

        verify(tableService).getTableCountByStatus(RestaurantTable.TableStatus.AVAILABLE, 1L);
    }

    @Test
    void checkTableNameExists_ExistingName_ReturnsTrue() throws Exception {
        when(tableService.existsByNameAndTenantId("Test Table", 1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/restaurant-tables/exists")
                        .param("name", "Test Table")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(tableService).existsByNameAndTenantId("Test Table", 1L);
    }

    @Test
    void checkTableNameExists_NonExistentName_ReturnsFalse() throws Exception {
        when(tableService.existsByNameAndTenantId("Non Existent", 1L)).thenReturn(false);

        mockMvc.perform(get("/api/v1/restaurant-tables/exists")
                        .param("name", "Non Existent")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(tableService).existsByNameAndTenantId("Non Existent", 1L);
    }

    @Test
    void accessWithoutAuthentication_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/restaurant-tables")
                        .param("tenantId", "1"))
                .andExpect(status().isOk()); // Security is disabled, so unauthenticated access returns 200 OK
    }

    @Test
    void accessWithoutTenantId_ReturnsOk() throws Exception {
        when(tableService.getAllTables()).thenReturn(testTables);

        mockMvc.perform(get("/api/v1/restaurant-tables"))
                .andExpect(status().isOk()); // Legacy method doesn't require tenantId
    }
} 