package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.AvailabilityDto;

import java.util.List;

public interface AvailabilityService {
    AvailabilityDto createAvailability(AvailabilityDto availabilityDto);
    AvailabilityDto getAvailabilityById(Long id);
    List<AvailabilityDto> getAllAvailabilities();
    List<AvailabilityDto> getAvailabilitiesBySpaceId(Long spaceId);
    AvailabilityDto updateAvailability(Long id, AvailabilityDto availabilityDto);
    void deleteAvailability(Long id);
}
