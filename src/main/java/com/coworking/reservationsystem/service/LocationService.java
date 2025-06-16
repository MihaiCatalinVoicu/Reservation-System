package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.LocationDto;

import java.util.List;

public interface LocationService {
    LocationDto createLocation(LocationDto locationDto);
    LocationDto getLocationById(Long id);
    List<LocationDto> getAllLocations();
    LocationDto updateLocation(Long id, LocationDto locationDto);
    void deleteLocation(Long id);
}
