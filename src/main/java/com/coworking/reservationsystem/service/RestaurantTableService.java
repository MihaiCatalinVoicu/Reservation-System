package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.RestaurantTableDto;
import com.coworking.reservationsystem.model.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableService {
    
    /**
     * Create a new restaurant table
     */
    RestaurantTableDto createTable(RestaurantTableDto tableDto);
    
    /**
     * Get table by ID and tenant ID
     */
    Optional<RestaurantTableDto> getTableById(Long id, Long tenantId);
    
    /**
     * Get table by ID (legacy method)
     */
    RestaurantTableDto getTableById(Long id);
    
    /**
     * Get all tables for a tenant
     */
    List<RestaurantTableDto> getAllTablesByTenant(Long tenantId);
    
    /**
     * Get all tables (legacy method)
     */
    List<RestaurantTableDto> getAllTables();
    
    /**
     * Get all tables for a space
     */
    List<RestaurantTableDto> getTablesBySpace(Long spaceId, Long tenantId);
    
    /**
     * Get all tables for a space (legacy method)
     */
    List<RestaurantTableDto> getTablesBySpace(Long spaceId);
    
    /**
     * Get all tables by status for a tenant
     */
    List<RestaurantTableDto> getTablesByStatus(RestaurantTable.TableStatus status, Long tenantId);
    
    /**
     * Get all tables by status (legacy method)
     */
    List<RestaurantTableDto> getTablesByStatus(RestaurantTable.TableStatus status);
    
    /**
     * Get available tables for a tenant
     */
    List<RestaurantTableDto> getAvailableTables(Long tenantId);
    
    /**
     * Get available tables (legacy method)
     */
    List<RestaurantTableDto> getAvailableTables();
    
    /**
     * Get available tables with minimum seats for a tenant
     */
    List<RestaurantTableDto> getAvailableTablesByMinSeats(Long tenantId, Integer minSeats);
    
    /**
     * Get available tables with minimum seats (legacy method)
     */
    List<RestaurantTableDto> getAvailableTablesByMinSeats(Integer minSeats);
    
    /**
     * Update table (legacy method)
     */
    RestaurantTableDto updateTable(Long id, RestaurantTableDto tableDto);
    
    /**
     * Update table status
     */
    Optional<RestaurantTableDto> updateTableStatus(Long id, RestaurantTable.TableStatus status, Long tenantId);
    
    /**
     * Update table status (legacy method)
     */
    RestaurantTableDto updateTableStatus(Long id, RestaurantTable.TableStatus status);
    
    /**
     * Delete table
     */
    boolean deleteTable(Long id, Long tenantId);
    
    /**
     * Delete table (legacy method)
     */
    void deleteTable(Long id);
    
    /**
     * Check if table exists by name and tenant ID
     */
    boolean existsByNameAndTenantId(String name, Long tenantId);
    
    /**
     * Get table count by tenant ID
     */
    long getTableCountByTenant(Long tenantId);
    
    /**
     * Get table count by status and tenant ID
     */
    long getTableCountByStatus(RestaurantTable.TableStatus status, Long tenantId);
} 