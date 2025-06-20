package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.RestaurantTableDto;
import com.coworking.reservationsystem.model.entity.RestaurantTable;
import com.coworking.reservationsystem.model.entity.Space;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.repository.RestaurantTableRepository;
import com.coworking.reservationsystem.repository.SpaceRepository;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final SpaceRepository spaceRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public RestaurantTableDto createTable(RestaurantTableDto tableDto) {
        // Validate space exists
        Space space = spaceRepository.findById(tableDto.spaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + tableDto.spaceId()));

        // Validate tenant exists
        Tenant tenant = tenantRepository.findById(tableDto.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tableDto.tenantId()));

        // Check if table with same name already exists for this tenant
        if (tableRepository.existsByNameAndTenantId(tableDto.name(), tableDto.tenantId())) {
            throw new ValidationException("Table with name " + tableDto.name() + " already exists for this tenant");
        }

        RestaurantTable table = RestaurantTableDto.Mapper.toEntity(tableDto);
        table.setSpace(space);
        table.setTenant(tenant);

        RestaurantTable savedTable = tableRepository.save(table);
        return RestaurantTableDto.Mapper.toDto(savedTable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantTableDto> getTableById(Long id, Long tenantId) {
        return tableRepository.findByIdAndTenantId(id, tenantId)
                .map(RestaurantTableDto.Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getAllTablesByTenant(Long tenantId) {
        return tableRepository.findByTenantIdOrderByName(tenantId).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getTablesBySpace(Long spaceId, Long tenantId) {
        return tableRepository.findBySpaceIdAndTenantIdOrderByName(spaceId, tenantId).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getTablesByStatus(RestaurantTable.TableStatus status, Long tenantId) {
        return tableRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getAvailableTables(Long tenantId) {
        return tableRepository.findByStatusAndTenantIdOrderByName(RestaurantTable.TableStatus.AVAILABLE, tenantId).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getAvailableTablesByMinSeats(Long tenantId, Integer minSeats) {
        return tableRepository.findAvailableTablesByMinSeats(tenantId, minSeats).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<RestaurantTableDto> updateTableStatus(Long id, RestaurantTable.TableStatus status, Long tenantId) {
        return tableRepository.findByIdAndTenantId(id, tenantId)
                .map(table -> {
                    table.setStatus(status);
                    RestaurantTable updatedTable = tableRepository.save(table);
                    return RestaurantTableDto.Mapper.toDto(updatedTable);
                });
    }

    @Override
    @Transactional
    public boolean deleteTable(Long id, Long tenantId) {
        Optional<RestaurantTable> table = tableRepository.findByIdAndTenantId(id, tenantId);
        if (table.isPresent()) {
            tableRepository.delete(table.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndTenantId(String name, Long tenantId) {
        return tableRepository.existsByNameAndTenantId(name, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTableCountByTenant(Long tenantId) {
        return tableRepository.countByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTableCountByStatus(RestaurantTable.TableStatus status, Long tenantId) {
        return tableRepository.countByStatusAndTenantId(status, tenantId);
    }

    // Legacy methods for backward compatibility
    @Override
    @Transactional(readOnly = true)
    public RestaurantTableDto getTableById(Long id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));
        return RestaurantTableDto.Mapper.toDto(table);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getAllTables() {
        return tableRepository.findAll().stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getTablesBySpace(Long spaceId) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new ResourceNotFoundException("Space not found with id: " + spaceId);
        }
        return tableRepository.findBySpaceIdOrderByName(spaceId).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getTablesByStatus(RestaurantTable.TableStatus status) {
        return tableRepository.findByStatusOrderByName(status).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getAvailableTables() {
        return tableRepository.findByStatusOrderByName(RestaurantTable.TableStatus.AVAILABLE).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableDto> getAvailableTablesByMinSeats(Integer minSeats) {
        return tableRepository.findAvailableTablesByMinSeats(minSeats).stream()
                .map(RestaurantTableDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RestaurantTableDto updateTable(Long id, RestaurantTableDto tableDto) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));

        // Validate space exists
        Space space = spaceRepository.findById(tableDto.spaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + tableDto.spaceId()));

        // Check if name is being changed and if it conflicts with existing table
        if (!table.getName().equals(tableDto.name()) &&
            tableRepository.existsByNameAndTenantId(tableDto.name(), tableDto.tenantId())) {
            throw new ValidationException("Table with name " + tableDto.name() + " already exists for this tenant");
        }

        table.setName(tableDto.name());
        table.setNumberOfSeats(tableDto.numberOfSeats());
        table.setStatus(tableDto.status());
        table.setNotes(tableDto.notes());
        table.setSpace(space);

        RestaurantTable updatedTable = tableRepository.save(table);
        return RestaurantTableDto.Mapper.toDto(updatedTable);
    }

    @Override
    @Transactional
    public RestaurantTableDto updateTableStatus(Long id, RestaurantTable.TableStatus status) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));
        
        table.setStatus(status);
        RestaurantTable updatedTable = tableRepository.save(table);
        return RestaurantTableDto.Mapper.toDto(updatedTable);
    }

    @Override
    @Transactional
    public void deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Table not found with id: " + id);
        }
        tableRepository.deleteById(id);
    }
} 