package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.model.entity.Location;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.LocationRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final LocationRepository locationRepository;
    private final TenantRepository tenantRepository;

    @Override
    public SpaceDto createSpace(SpaceDto spaceDto) {
        Location location = locationRepository.findById(spaceDto.locationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + spaceDto.locationId()));

        Tenant tenant = null;
        if (spaceDto.tenantId() != null) {
            tenant = tenantRepository.findById(spaceDto.tenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + spaceDto.tenantId()));
        }

        Space space = new Space();
        space.setName(spaceDto.name());
        space.setDescription(spaceDto.description());
        space.setLocation(location);
        space.setCapacity(spaceDto.capacity());
        space.setPricePerHour(spaceDto.pricePerHour());
        space.setTenant(tenant);
        
        Space savedSpace = spaceRepository.save(space);
        
        return SpaceDto.Mapper.toDto(savedSpace);
    }

    @Override
    public Optional<SpaceDto> getSpaceById(Long id, Long tenantId) {
        return spaceRepository.findByIdAndTenantId(id, tenantId)
                .map(SpaceDto.Mapper::toDto);
    }

    @Override
    public List<SpaceDto> getAllSpaces(Long tenantId) {
        return spaceRepository.findByTenantId(tenantId).stream()
                .map(SpaceDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceDto> getSpacesByLocation(Long locationId, Long tenantId) {
        return spaceRepository.findByLocationIdAndTenantId(locationId, tenantId).stream()
                .map(SpaceDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceDto> getSpacesByCapacity(Integer capacity, Long tenantId) {
        return spaceRepository.findByCapacityAndTenantId(capacity, tenantId).stream()
                .map(SpaceDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SpaceDto> updateSpace(Long id, SpaceDto spaceDto) {
        return spaceRepository.findByIdAndTenantId(id, spaceDto.tenantId())
                .map(space -> {
                    Location location = locationRepository.findById(spaceDto.locationId())
                            .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + spaceDto.locationId()));
                    
                    space.setName(spaceDto.name());
                    space.setDescription(spaceDto.description());
                    space.setLocation(location);
                    space.setCapacity(spaceDto.capacity());
                    space.setPricePerHour(spaceDto.pricePerHour());
                    
                    Space updatedSpace = spaceRepository.save(space);
                    return SpaceDto.Mapper.toDto(updatedSpace);
                });
    }

    @Override
    public boolean deleteSpace(Long id, Long tenantId) {
        Optional<Space> space = spaceRepository.findByIdAndTenantId(id, tenantId);
        if (space.isPresent()) {
            spaceRepository.delete(space.get());
            return true;
        }
        return false;
    }

    // Legacy methods for backward compatibility
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
    public List<SpaceDto> getSpacesByLocationId(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Location not found with id: " + locationId);
        }
        return spaceRepository.findByLocationId(locationId).stream()
                .map(SpaceDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSpace(Long id) {
        if (!spaceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Space not found with id: " + id);
        }
        spaceRepository.deleteById(id);
    }

    @Override
    public List<SpaceDto> getSpacesByTenantId(Long tenantId) {
        return spaceRepository.findByTenantId(tenantId).stream()
                .map(SpaceDto.Mapper::toDto)
                .toList();
    }
} 