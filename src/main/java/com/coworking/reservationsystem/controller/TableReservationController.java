package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.TableReservationDto;
import com.coworking.reservationsystem.model.entity.TableReservation;
import com.coworking.reservationsystem.service.TableReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/table-reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class TableReservationController {

    private final TableReservationService reservationService;

    @PostMapping
    public ResponseEntity<TableReservationDto> createTableReservation(@Valid @RequestBody TableReservationDto reservationDto) {
        TableReservationDto createdReservation = reservationService.createTableReservation(reservationDto);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableReservationDto> getTableReservationById(@PathVariable Long id, @RequestParam Long tenantId) {
        Optional<TableReservationDto> reservation = reservationService.getTableReservationById(id, tenantId);
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TableReservationDto>> getAllTableReservations(@RequestParam Long tenantId) {
        List<TableReservationDto> reservations = reservationService.getAllTableReservationsByTenant(tenantId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TableReservationDto>> getTableReservationsByCustomer(@PathVariable Long customerId, @RequestParam Long tenantId) {
        List<TableReservationDto> reservations = reservationService.getTableReservationsByCustomer(customerId, tenantId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<TableReservationDto>> getTableReservationsByTable(@PathVariable Long tableId, @RequestParam Long tenantId) {
        List<TableReservationDto> reservations = reservationService.getTableReservationsByTable(tableId, tenantId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TableReservationDto>> getTableReservationsByStatus(@PathVariable TableReservation.TableReservationStatus status, @RequestParam Long tenantId) {
        List<TableReservationDto> reservations = reservationService.getTableReservationsByStatus(status, tenantId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TableReservationDto>> getPendingTableReservations(@RequestParam Long tenantId) {
        List<TableReservationDto> reservations = reservationService.getPendingTableReservations(tenantId);
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableReservationDto> updateTableReservation(@PathVariable Long id, @Valid @RequestBody TableReservationDto reservationDto) {
        return reservationService.updateTableReservation(id, reservationDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<TableReservationDto> confirmTableReservation(@PathVariable Long id, @RequestParam Long tenantId) {
        return reservationService.confirmTableReservation(id, tenantId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<TableReservationDto> rejectTableReservation(@PathVariable Long id, @RequestParam Long tenantId) {
        return reservationService.rejectTableReservation(id, tenantId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TableReservationDto> cancelTableReservation(@PathVariable Long id, @RequestParam Long tenantId) {
        return reservationService.cancelTableReservation(id, tenantId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TableReservationDto> completeTableReservation(@PathVariable Long id, @RequestParam Long tenantId) {
        return reservationService.completeTableReservation(id, tenantId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTableReservation(@PathVariable Long id, @RequestParam Long tenantId) {
        boolean deleted = reservationService.deleteTableReservation(id, tenantId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TableReservationDto>> getTableReservationsByDateRange(@RequestParam LocalDateTime startDate, 
                                                                                     @RequestParam LocalDateTime endDate, 
                                                                                     @RequestParam Long tenantId) {
        List<TableReservationDto> reservations = reservationService.getTableReservationsByDateRange(startDate, endDate, tenantId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/customer/{customerId}/page")
    public ResponseEntity<Page<TableReservationDto>> getTableReservationsByCustomer(@PathVariable Long customerId, 
                                                                                    @RequestParam Long tenantId, 
                                                                                    Pageable pageable) {
        Page<TableReservationDto> reservations = reservationService.getTableReservationsByCustomer(customerId, tenantId, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/overlapping")
    public ResponseEntity<Boolean> checkOverlappingReservations(@RequestParam Long tableId, 
                                                               @RequestParam LocalDateTime startTime, 
                                                               @RequestParam LocalDateTime endTime) {
        boolean hasOverlapping = reservationService.hasOverlappingReservations(tableId, startTime, endTime);
        return ResponseEntity.ok(hasOverlapping);
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getTableReservationCountByStatus(@PathVariable TableReservation.TableReservationStatus status, @RequestParam Long tenantId) {
        long count = reservationService.getTableReservationCountByStatus(status, tenantId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTableReservationCount(@RequestParam Long tenantId) {
        long count = reservationService.getTableReservationCountByTenant(tenantId);
        return ResponseEntity.ok(count);
    }
} 