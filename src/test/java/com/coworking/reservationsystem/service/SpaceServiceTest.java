package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.SpaceDto;
import com.coworking.reservationsystem.model.entity.Location;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.LocationRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.impl.SpaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private SpaceServiceImpl spaceService;

    private Tenant tenant;
    private Location location;
    private Space space;
    private SpaceDto spaceDto;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("hotel-exemplu");
        tenant.setSubdomain("hotel-exemplu");

        location = new Location();
        location.setId(1L);
        location.setName("Sala Mare");
        location.setTenant(tenant);

        space = new Space();
        space.setId(1L);
        space.setName("Birou 1");
        space.setDescription("Descriere birou");
        space.setCapacity(10);
        space.setLocation(location);
        space.setTenant(tenant);
        space.setPricePerHour(100.0);

        spaceDto = new SpaceDto(
                1L,
                "Birou 1",
                "Descriere birou",
                10,
                1L,
                100.0,
                1L
        );
    }

    @Test
    void createSpace_Success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(spaceRepository.save(any(Space.class))).thenReturn(space);

        SpaceDto result = spaceService.createSpace(spaceDto);

        assertNotNull(result);
        assertEquals(spaceDto.name(), result.name());
        assertEquals(spaceDto.tenantId(), result.tenantId());
        verify(spaceRepository).save(any(Space.class));
    }

    @Test
    void createSpace_LocationNotFound_ThrowsException() {
        SpaceDto dto = new SpaceDto(null, "Birou", "Desc", 5, 999L, 50.0, 1L);
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> spaceService.createSpace(dto));
        verify(spaceRepository, never()).save(any(Space.class));
    }

    @Test
    void createSpace_TenantNotFound_ThrowsException() {
        SpaceDto dto = new SpaceDto(null, "Birou", "Desc", 5, 1L, 50.0, 999L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> spaceService.createSpace(dto));
        verify(spaceRepository, never()).save(any(Space.class));
    }

    @Test
    void getSpaceById_Success() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(space));
        SpaceDto result = spaceService.getSpaceById(1L);
        assertNotNull(result);
        assertEquals(space.getId(), result.id());
        verify(spaceRepository).findById(1L);
    }

    @Test
    void getSpaceById_NotFound_ThrowsException() {
        when(spaceRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> spaceService.getSpaceById(999L));
        verify(spaceRepository).findById(999L);
    }

    @Test
    void getAllSpaces_Success() {
        Space space2 = new Space();
        space2.setId(2L);
        space2.setName("Birou 2");
        space2.setLocation(location);
        space2.setTenant(tenant);
        List<Space> spaces = Arrays.asList(space, space2);
        when(spaceRepository.findAll()).thenReturn(spaces);
        List<SpaceDto> result = spaceService.getAllSpaces();
        assertEquals(2, result.size());
        verify(spaceRepository).findAll();
    }

    @Test
    void getSpacesByLocationId_Success() {
        when(locationRepository.existsById(1L)).thenReturn(true);
        when(spaceRepository.findByLocationId(1L)).thenReturn(List.of(space));
        List<SpaceDto> result = spaceService.getSpacesByLocationId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).locationId());
        verify(locationRepository).existsById(1L);
        verify(spaceRepository).findByLocationId(1L);
    }

    @Test
    void getSpacesByLocationId_LocationNotFound_ThrowsException() {
        when(locationRepository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> spaceService.getSpacesByLocationId(999L));
        verify(locationRepository).existsById(999L);
        verify(spaceRepository, never()).findByLocationId(any());
    }

    @Test
    void getSpacesByTenantId_Success() {
        when(spaceRepository.findByTenantId(1L)).thenReturn(List.of(space));
        List<SpaceDto> result = spaceService.getSpacesByTenantId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).tenantId());
        verify(spaceRepository).findByTenantId(1L);
    }
} 