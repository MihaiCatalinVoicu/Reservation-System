package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.model.dto.LocationDto;
import com.coworking.reservationsystem.model.entity.Location;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.LocationRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.impl.LocationServiceImpl;
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
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private LocationServiceImpl locationService;

    private Tenant tenant;
    private Location location;
    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("hotel-exemplu");
        tenant.setSubdomain("hotel-exemplu");

        location = new Location();
        location.setId(1L);
        location.setName("Sala Mare");
        location.setAddress("Strada Exemplu 1");
        location.setCity("București");
        location.setTenant(tenant);

        locationDto = new LocationDto(
                1L,
                "Sala Mare",
                "Strada Exemplu 1",
                "București",
                1L
        );
    }

    @Test
    void createLocation_Success() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDto result = locationService.createLocation(locationDto);

        assertNotNull(result);
        assertEquals(locationDto.name(), result.name());
        assertEquals(locationDto.tenantId(), result.tenantId());
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void createLocation_TenantNotFound_ThrowsException() {
        LocationDto dto = new LocationDto(null, "Sala", "Adresa", "Oras", 999L);
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationService.createLocation(dto));
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void getLocationById_Success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        LocationDto result = locationService.getLocationById(1L);
        assertNotNull(result);
        assertEquals(location.getId(), result.id());
        verify(locationRepository).findById(1L);
    }

    @Test
    void getLocationById_NotFound_ThrowsException() {
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationById(999L));
        verify(locationRepository).findById(999L);
    }

    @Test
    void getAllLocations_Success() {
        Location location2 = new Location();
        location2.setId(2L);
        location2.setName("Sala Mica");
        location2.setAddress("Strada 2");
        location2.setCity("București");
        location2.setTenant(tenant);
        List<Location> locations = Arrays.asList(location, location2);
        when(locationRepository.findAll()).thenReturn(locations);
        List<LocationDto> result = locationService.getAllLocations();
        assertEquals(2, result.size());
        verify(locationRepository).findAll();
    }

    @Test
    void getLocationsByTenantId_Success() {
        when(locationRepository.findByTenantId(1L)).thenReturn(List.of(location));
        List<LocationDto> result = locationService.getLocationsByTenantId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).tenantId());
        verify(locationRepository).findByTenantId(1L);
    }
} 