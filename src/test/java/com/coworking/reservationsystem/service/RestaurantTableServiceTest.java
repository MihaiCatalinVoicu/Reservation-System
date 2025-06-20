package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.RestaurantTableDto;
import com.coworking.reservationsystem.model.entity.RestaurantTable;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.RestaurantTableRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.impl.RestaurantTableServiceImpl;
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
class RestaurantTableServiceTest {

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private RestaurantTableServiceImpl tableService;

    private RestaurantTable testTable;
    private RestaurantTableDto testTableDto;
    private Space testSpace;
    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setName("Test Restaurant");

        testSpace = new Space();
        testSpace.setId(1L);
        testSpace.setName("Test Space");
        testSpace.setTenant(testTenant);

        testTable = new RestaurantTable();
        testTable.setId(1L);
        testTable.setName("Test Table");
        testTable.setNumberOfSeats(4);
        testTable.setStatus(RestaurantTable.TableStatus.AVAILABLE);
        testTable.setSpace(testSpace);
        testTable.setTenant(testTenant);
        testTable.setNotes("Test notes");
        testTable.setCreatedAt(LocalDateTime.now());
        testTable.setUpdatedAt(LocalDateTime.now());

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
    }

    @Test
    void createTable_ValidTable_ReturnsCreatedTable() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(testSpace));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(tableRepository.existsByNameAndTenantId("Test Table", 1L)).thenReturn(false);
        when(tableRepository.save(any(RestaurantTable.class))).thenReturn(testTable);

        RestaurantTableDto result = tableService.createTable(testTableDto);

        assertNotNull(result);
        assertEquals("Test Table", result.name());
        assertEquals(4, result.numberOfSeats());
        assertEquals(RestaurantTable.TableStatus.AVAILABLE, result.status());
        verify(tableRepository).save(any(RestaurantTable.class));
    }

    @Test
    void createTable_SpaceNotFound_ThrowsResourceNotFoundException() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tableService.createTable(testTableDto);
        });

        verify(tableRepository, never()).save(any());
    }

    @Test
    void createTable_TenantNotFound_ThrowsResourceNotFoundException() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(testSpace));
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tableService.createTable(testTableDto);
        });

        verify(tableRepository, never()).save(any());
    }

    @Test
    void createTable_TableNameExists_ThrowsValidationException() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(testSpace));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(tableRepository.existsByNameAndTenantId("Test Table", 1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> {
            tableService.createTable(testTableDto);
        });

        verify(tableRepository, never()).save(any());
    }

    @Test
    void getTableById_ExistingTable_ReturnsTable() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));

        Optional<RestaurantTableDto> result = tableService.getTableById(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals("Test Table", result.get().name());
    }

    @Test
    void getTableById_NonExistentTable_ReturnsEmpty() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.empty());

        Optional<RestaurantTableDto> result = tableService.getTableById(1L, 1L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllTablesByTenant_ReturnsTablesList() {
        List<RestaurantTable> tables = Arrays.asList(testTable);
        when(tableRepository.findByTenantIdOrderByName(1L)).thenReturn(tables);

        List<RestaurantTableDto> result = tableService.getAllTablesByTenant(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Table", result.get(0).name());
    }

    @Test
    void getTablesBySpace_ReturnsTablesList() {
        List<RestaurantTable> tables = Arrays.asList(testTable);
        when(tableRepository.findBySpaceIdAndTenantIdOrderByName(1L, 1L)).thenReturn(tables);

        List<RestaurantTableDto> result = tableService.getTablesBySpace(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Table", result.get(0).name());
    }

    @Test
    void getTablesByStatus_ReturnsTablesList() {
        List<RestaurantTable> tables = Arrays.asList(testTable);
        when(tableRepository.findByStatusAndTenantId(RestaurantTable.TableStatus.AVAILABLE, 1L)).thenReturn(tables);

        List<RestaurantTableDto> result = tableService.getTablesByStatus(RestaurantTable.TableStatus.AVAILABLE, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RestaurantTable.TableStatus.AVAILABLE, result.get(0).status());
    }

    @Test
    void getAvailableTables_ReturnsAvailableTables() {
        List<RestaurantTable> tables = Arrays.asList(testTable);
        when(tableRepository.findByStatusAndTenantIdOrderByName(RestaurantTable.TableStatus.AVAILABLE, 1L)).thenReturn(tables);

        List<RestaurantTableDto> result = tableService.getAvailableTables(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RestaurantTable.TableStatus.AVAILABLE, result.get(0).status());
    }

    @Test
    void getAvailableTablesByMinSeats_ReturnsTablesWithMinSeats() {
        List<RestaurantTable> tables = Arrays.asList(testTable);
        when(tableRepository.findAvailableTablesByMinSeats(1L, 4)).thenReturn(tables);

        List<RestaurantTableDto> result = tableService.getAvailableTablesByMinSeats(1L, 4);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).numberOfSeats());
    }

    @Test
    void updateTable_ValidTable_ReturnsUpdatedTable() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(testTable));
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(testSpace));
        when(tableRepository.existsByNameAndTenantId("Updated Table", 1L)).thenReturn(false);
        when(tableRepository.save(any(RestaurantTable.class))).thenReturn(testTable);

        RestaurantTableDto updateDto = new RestaurantTableDto(
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

        RestaurantTableDto result = tableService.updateTable(1L, updateDto);

        assertNotNull(result);
        verify(tableRepository).save(any(RestaurantTable.class));
    }

    @Test
    void updateTable_NonExistentTable_ThrowsResourceNotFoundException() {
        when(tableRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tableService.updateTable(1L, testTableDto);
        });
        verify(tableRepository, never()).save(any());
    }

    @Test
    void updateTableStatus_ValidStatus_ReturnsUpdatedTable() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));
        when(tableRepository.save(any(RestaurantTable.class))).thenReturn(testTable);

        Optional<RestaurantTableDto> result = tableService.updateTableStatus(1L, RestaurantTable.TableStatus.OCCUPIED, 1L);

        assertTrue(result.isPresent());
        verify(tableRepository).save(any(RestaurantTable.class));
    }

    @Test
    void deleteTable_ExistingTable_ReturnsTrue() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(testTable));
        doNothing().when(tableRepository).delete(testTable);

        boolean result = tableService.deleteTable(1L, 1L);

        assertTrue(result);
        verify(tableRepository).delete(testTable);
    }

    @Test
    void deleteTable_NonExistentTable_ReturnsFalse() {
        when(tableRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.empty());

        boolean result = tableService.deleteTable(1L, 1L);

        assertFalse(result);
        verify(tableRepository, never()).delete(any());
    }

    @Test
    void existsByNameAndTenantId_ReturnsBoolean() {
        when(tableRepository.existsByNameAndTenantId("Test Table", 1L)).thenReturn(true);

        boolean result = tableService.existsByNameAndTenantId("Test Table", 1L);

        assertTrue(result);
    }

    @Test
    void getTableCountByTenant_ReturnsCount() {
        when(tableRepository.countByTenantId(1L)).thenReturn(5L);

        long result = tableService.getTableCountByTenant(1L);

        assertEquals(5L, result);
    }

    @Test
    void getTableCountByStatus_ReturnsCount() {
        when(tableRepository.countByStatusAndTenantId(RestaurantTable.TableStatus.AVAILABLE, 1L)).thenReturn(3L);

        long result = tableService.getTableCountByStatus(RestaurantTable.TableStatus.AVAILABLE, 1L);

        assertEquals(3L, result);
    }
} 