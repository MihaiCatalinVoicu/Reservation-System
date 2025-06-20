package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.SpaceDto;

import java.util.List;
import java.util.Optional;

public interface SpaceService {
    SpaceDto createSpace(SpaceDto spaceDto);
    Optional<SpaceDto> getSpaceById(Long id, Long tenantId);
    List<SpaceDto> getAllSpaces(Long tenantId);
    List<SpaceDto> getSpacesByLocation(Long locationId, Long tenantId);
    List<SpaceDto> getSpacesByCapacity(Integer capacity, Long tenantId);
    Optional<SpaceDto> updateSpace(Long id, SpaceDto spaceDto);
    boolean deleteSpace(Long id, Long tenantId);
    
    // Legacy methods for backward compatibility
    SpaceDto getSpaceById(Long id);
    List<SpaceDto> getAllSpaces();
    List<SpaceDto> getSpacesByLocationId(Long locationId);
    List<SpaceDto> getSpacesByTenantId(Long tenantId);
    List<SpaceDto> getSpacesByCapacity(Integer capacity);
    void deleteSpace(Long id);
}
