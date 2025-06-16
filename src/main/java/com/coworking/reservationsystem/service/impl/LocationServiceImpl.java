package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.LocationDto;
import com.coworking.reservationsystem.model.entity.Location;
import com.coworking.reservationsystem.repository.LocationRepository;
import com.coworking.reservationsystem.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationDto createLocation(LocationDto locationDto) {
        Location location = LocationDto.Mapper.toEntity(locationDto);
        location = locationRepository.save(location);
        return LocationDto.Mapper.toDto(location);
    }

    @Override
    public LocationDto getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return LocationDto.Mapper.toDto(location);
    }

    @Override
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LocationDto updateLocation(Long id, LocationDto locationDto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        location.setName(locationDto.name());
        location.setAddress(locationDto.address());
        location.setCity(locationDto.city());
        location = locationRepository.save(location);
        return LocationDto.Mapper.toDto(location);
    }

    @Override
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
} 