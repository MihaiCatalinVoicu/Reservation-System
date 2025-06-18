package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping
    public ResponseEntity<SpaceDto> createSpace(@Valid @RequestBody SpaceDto spaceDto) {
        SpaceDto createdSpace = spaceService.createSpace(spaceDto);
        return new ResponseEntity<>(createdSpace, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceDto> getSpaceById(@PathVariable Long id, @RequestParam Long tenantId) {
        Optional<SpaceDto> space = spaceService.getSpaceById(id, tenantId);
        return space.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SpaceDto>> getAllSpaces(@RequestParam Long tenantId) {
        List<SpaceDto> spaces = spaceService.getAllSpaces(tenantId);
        return ResponseEntity.ok(spaces);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceDto> updateSpace(@PathVariable Long id, @Valid @RequestBody SpaceDto spaceDto) {
        return spaceService.updateSpace(id, spaceDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id, @RequestParam Long tenantId) {
        boolean deleted = spaceService.deleteSpace(id, tenantId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<SpaceDto>> getSpacesByLocation(@PathVariable Long locationId, @RequestParam Long tenantId) {
        List<SpaceDto> spaces = spaceService.getSpacesByLocation(locationId, tenantId);
        return ResponseEntity.ok(spaces);
    }

    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<SpaceDto>> getSpacesByCapacity(@PathVariable Integer capacity, @RequestParam Long tenantId) {
        List<SpaceDto> spaces = spaceService.getSpacesByCapacity(capacity, tenantId);
        return ResponseEntity.ok(spaces);
    }
}
