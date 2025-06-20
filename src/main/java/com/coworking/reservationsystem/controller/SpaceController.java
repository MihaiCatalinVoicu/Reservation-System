package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<SpaceDto> getSpaceById(@PathVariable Long id) {
        try {
            SpaceDto space = spaceService.getSpaceById(id);
            return ResponseEntity.ok(space);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SpaceDto>> getAllSpaces() {
        List<SpaceDto> spaces = spaceService.getAllSpaces();
        return ResponseEntity.ok(spaces);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<SpaceDto>> getSpacesByTenantId(@PathVariable Long tenantId) {
        List<SpaceDto> spaces = spaceService.getSpacesByTenantId(tenantId);
        return ResponseEntity.ok(spaces);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceDto> updateSpace(@PathVariable Long id, @Valid @RequestBody SpaceDto spaceDto) {
        return spaceService.updateSpace(id, spaceDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        try {
            spaceService.deleteSpace(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<SpaceDto>> getSpacesByLocation(@PathVariable Long locationId) {
        List<SpaceDto> spaces = spaceService.getSpacesByLocationId(locationId);
        return ResponseEntity.ok(spaces);
    }

    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<SpaceDto>> getSpacesByCapacity(@PathVariable Integer capacity) {
        List<SpaceDto> spaces = spaceService.getSpacesByCapacity(capacity);
        return ResponseEntity.ok(spaces);
    }
}
