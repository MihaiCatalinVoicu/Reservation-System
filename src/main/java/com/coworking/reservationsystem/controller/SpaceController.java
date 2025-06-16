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
        SpaceDto space = spaceService.getSpaceById(id);
        return ResponseEntity.ok(space);
    }

    @GetMapping
    public ResponseEntity<List<SpaceDto>> getAllSpaces() {
        List<SpaceDto> spaces = spaceService.getAllSpaces();
        return ResponseEntity.ok(spaces);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceDto> updateSpace(@PathVariable Long id, @Valid @RequestBody SpaceDto spaceDto) {
        SpaceDto updatedSpace = spaceService.updateSpace(id, spaceDto);
        return ResponseEntity.ok(updatedSpace);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        spaceService.deleteSpace(id);
        return ResponseEntity.noContent().build();
    }
}
