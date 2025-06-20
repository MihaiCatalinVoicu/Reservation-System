package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.LocationDto;
import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.service.LocationService;
import com.coworking.reservationsystem.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final SpaceService spaceService;

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationDto locationDto) {
        LocationDto createdLocation = locationService.createLocation(locationDto);
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        try {
            LocationDto location = locationService.getLocationById(id);
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        List<LocationDto> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}/spaces")
    public ResponseEntity<List<SpaceDto>> getSpacesByLocationId(@PathVariable Long id) {
        List<SpaceDto> spaces = spaceService.getSpacesByLocationId(id);
        return ResponseEntity.ok(spaces);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationDto locationDto) {
        try {
            LocationDto updatedLocation = locationService.updateLocation(id, locationDto);
            return ResponseEntity.ok(updatedLocation);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
