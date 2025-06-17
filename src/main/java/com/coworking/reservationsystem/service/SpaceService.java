package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.SpaceDto;

import java.util.List;

public interface SpaceService {
    SpaceDto createSpace(SpaceDto spaceDto);
    SpaceDto getSpaceById(Long id);
    List<SpaceDto> getAllSpaces();
    List<SpaceDto> getSpacesByLocationId(Long locationId);
    SpaceDto updateSpace(Long id, SpaceDto spaceDto);
    void deleteSpace(Long id);
}
