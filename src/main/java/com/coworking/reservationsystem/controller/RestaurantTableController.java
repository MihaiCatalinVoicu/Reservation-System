package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.RestaurantTableDto;
import com.coworking.reservationsystem.model.entity.RestaurantTable;
import com.coworking.reservationsystem.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tables")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @PostMapping
    public ResponseEntity<RestaurantTableDto> createTable(@Valid @RequestBody RestaurantTableDto tableDto) {
        RestaurantTableDto createdTable = tableService.createTable(tableDto);
        return new ResponseEntity<>(createdTable, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableDto> getTableById(@PathVariable Long id, @RequestParam Long tenantId) {
        Optional<RestaurantTableDto> table = tableService.getTableById(id, tenantId);
        return table.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTableDto>> getAllTables(@RequestParam Long tenantId) {
        List<RestaurantTableDto> tables = tableService.getAllTablesByTenant(tenantId);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<RestaurantTableDto>> getTablesBySpace(@PathVariable Long spaceId, @RequestParam Long tenantId) {
        List<RestaurantTableDto> tables = tableService.getTablesBySpace(spaceId, tenantId);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RestaurantTableDto>> getTablesByStatus(@PathVariable RestaurantTable.TableStatus status, @RequestParam Long tenantId) {
        List<RestaurantTableDto> tables = tableService.getTablesByStatus(status, tenantId);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RestaurantTableDto>> getAvailableTables(@RequestParam Long tenantId) {
        List<RestaurantTableDto> tables = tableService.getAvailableTables(tenantId);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/available/min-seats/{minSeats}")
    public ResponseEntity<List<RestaurantTableDto>> getAvailableTablesByMinSeats(@PathVariable Integer minSeats, @RequestParam Long tenantId) {
        List<RestaurantTableDto> tables = tableService.getAvailableTablesByMinSeats(tenantId, minSeats);
        return ResponseEntity.ok(tables);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTableDto> updateTable(@PathVariable Long id, @Valid @RequestBody RestaurantTableDto tableDto) {
        return tableService.updateTable(id, tableDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RestaurantTableDto> updateTableStatus(@PathVariable Long id, 
                                                               @RequestParam RestaurantTable.TableStatus status, 
                                                               @RequestParam Long tenantId) {
        return tableService.updateTableStatus(id, status, tenantId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id, @RequestParam Long tenantId) {
        boolean deleted = tableService.deleteTable(id, tenantId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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