package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.ReservationDto;
import com.coworking.reservationsystem.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody ReservationDto reservationDto) {
        try {
            return new ResponseEntity<>(reservationService.createReservation(reservationDto), HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.getReservationById(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(reservationService.getReservationsByCustomerId(customerId));
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<ReservationDto>> getReservationsBySpaceId(@PathVariable Long spaceId) {
        return ResponseEntity.ok(reservationService.getReservationsBySpaceId(spaceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationDto reservationDto) {
        try {
            return ResponseEntity.ok(reservationService.updateReservation(id, reservationDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.confirmReservation(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.cancelReservation(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
