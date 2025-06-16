package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.AvailabilityDto;
import com.coworking.reservationsystem.model.entity.Availability;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.repository.AvailabilityRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final SpaceRepository spaceRepository;

    @Override
    public AvailabilityDto createAvailability(AvailabilityDto availabilityDto) {
        Space space = spaceRepository.findById(availabilityDto.spaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + availabilityDto.spaceId()));
        
        Availability availability = AvailabilityDto.Mapper.toEntity(availabilityDto, space);
        Availability savedAvailability = availabilityRepository.save(availability);
        return AvailabilityDto.Mapper.toDto(savedAvailability);
    }

    @Override
    public AvailabilityDto getAvailabilityById(Long id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + id));
        return AvailabilityDto.Mapper.toDto(availability);
    }

    @Override
    public List<AvailabilityDto> getAllAvailabilities() {
        return availabilityRepository.findAll().stream()
                .map(AvailabilityDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityDto> getAvailabilitiesBySpaceId(Long spaceId) {
        return availabilityRepository.findBySpaceId(spaceId).stream()
                .map(AvailabilityDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AvailabilityDto updateAvailability(Long id, AvailabilityDto availabilityDto) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + id));

        Space space = spaceRepository.findById(availabilityDto.spaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + availabilityDto.spaceId()));

        availability.setSpace(space);
        availability.setStartTime(availabilityDto.startTime());
        availability.setEndTime(availabilityDto.endTime());

        Availability updatedAvailability = availabilityRepository.save(availability);
        return AvailabilityDto.Mapper.toDto(updatedAvailability);
    }

    @Override
    public void deleteAvailability(Long id) {
        if (!availabilityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Availability not found with id: " + id);
        }
        availabilityRepository.deleteById(id);
    }
} 