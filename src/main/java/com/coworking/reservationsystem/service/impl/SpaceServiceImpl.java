package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.model.entity.Location;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.repository.LocationRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final LocationRepository locationRepository;

    @Override
    public SpaceDto createSpace(SpaceDto spaceDto) {
        Location location = locationRepository.findById(spaceDto.locationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + spaceDto.locationId()));

        Space space = new Space();
        space.setName(spaceDto.name());
        space.setDescription(spaceDto.description());
        space.setLocation(location);
        space.setCapacity(spaceDto.capacity());
        space.setPricePerHour(spaceDto.pricePerHour());
        
        Space savedSpace = spaceRepository.save(space);
        
        return SpaceDto.Mapper.toDto(savedSpace);
    }

    @Override
    public SpaceDto getSpaceById(Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + id));
        
        return SpaceDto.Mapper.toDto(space);
    }

    @Override
    public List<SpaceDto> getAllSpaces() {
        return spaceRepository.findAll().stream()
                .map(SpaceDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SpaceDto updateSpace(Long id, SpaceDto spaceDto) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + id));
        
        Location location = locationRepository.findById(spaceDto.locationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + spaceDto.locationId()));
        
        space.setName(spaceDto.name());
        space.setDescription(spaceDto.description());
        space.setLocation(location);
        space.setCapacity(spaceDto.capacity());
        space.setPricePerHour(spaceDto.pricePerHour());
        
        Space updatedSpace = spaceRepository.save(space);
        
        return SpaceDto.Mapper.toDto(updatedSpace);
    }

    @Override
    public void deleteSpace(Long id) {
        if (!spaceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Space not found with id: " + id);
        }
        spaceRepository.deleteById(id);
    }
} 