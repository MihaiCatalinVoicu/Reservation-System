package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.RestaurantTableDto;
import com.coworking.reservationsystem.model.entity.RestaurantTable;
import com.coworking.reservationsystem.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurant-tables")
@RequiredArgsConstructor
@Tag(name = "Restaurant Tables", description = "Restaurant table management APIs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @PostMapping
    public ResponseEntity<RestaurantTableDto> createTable(@Valid @RequestBody RestaurantTableDto tableDto) {
        RestaurantTableDto createdTable = tableService.createTable(tableDto);
        return new ResponseEntity<>(createdTable, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableDto> getTableById(@PathVariable Long id) {
        try {
            RestaurantTableDto table = tableService.getTableById(id);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTableDto>> getAllTables() {
        List<RestaurantTableDto> tables = tableService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RestaurantTableDto>> getAllTablesByTenant(@PathVariable Long tenantId) {
        List<RestaurantTableDto> tables = tableService.getAllTablesByTenant(tenantId);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<RestaurantTableDto>> getTablesBySpace(@PathVariable Long spaceId) {
        List<RestaurantTableDto> tables = tableService.getTablesBySpace(spaceId);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RestaurantTableDto>> getTablesByStatus(@PathVariable RestaurantTable.TableStatus status) {
        List<RestaurantTableDto> tables = tableService.getTablesByStatus(status);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RestaurantTableDto>> getAvailableTables() {
        List<RestaurantTableDto> tables = tableService.getAvailableTables();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/available/min-seats/{minSeats}")
    public ResponseEntity<List<RestaurantTableDto>> getAvailableTablesByMinSeats(@PathVariable Integer minSeats) {
        List<RestaurantTableDto> tables = tableService.getAvailableTablesByMinSeats(minSeats);
        return ResponseEntity.ok(tables);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTableDto> updateTable(@PathVariable Long id, @Valid @RequestBody RestaurantTableDto tableDto) {
        try {
            RestaurantTableDto updatedTable = tableService.updateTable(id, tableDto);
            return ResponseEntity.ok(updatedTable);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RestaurantTableDto> updateTableStatus(@PathVariable Long id, 
                                                               @RequestParam RestaurantTable.TableStatus status) {
        try {
            RestaurantTableDto updatedTable = tableService.updateTableStatus(id, status);
            return ResponseEntity.ok(updatedTable);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name, @RequestParam Long tenantId) {
        boolean exists = tableService.existsByNameAndTenantId(name, tenantId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTableCount(@RequestParam Long tenantId) {
        long count = tableService.getTableCountByTenant(tenantId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getTableCountByStatus(@PathVariable RestaurantTable.TableStatus status, @RequestParam Long tenantId) {
        long count = tableService.getTableCountByStatus(status, tenantId);
        return ResponseEntity.ok(count);
    }
} 