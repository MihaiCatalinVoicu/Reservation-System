package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.AvailabilityDto;
import com.coworking.reservationsystem.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityDto> createAvailability(@Valid @RequestBody AvailabilityDto availabilityDto) {
        return new ResponseEntity<>(availabilityService.createAvailability(availabilityDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityDto> getAvailabilityById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(id));
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityDto>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<AvailabilityDto>> getAvailabilitiesBySpaceId(@PathVariable Long spaceId) {
        return ResponseEntity.ok(availabilityService.getAvailabilitiesBySpaceId(spaceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityDto> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityDto availabilityDto) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, availabilityDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
