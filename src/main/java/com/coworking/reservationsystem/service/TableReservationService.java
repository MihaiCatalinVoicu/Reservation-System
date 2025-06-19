package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.TableReservationDto;
import com.coworking.reservationsystem.model.entity.TableReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TableReservationService {
    
    /**
     * Create a new table reservation
     */
    TableReservationDto createTableReservation(TableReservationDto reservationDto);
    
    /**
     * Get table reservation by ID and tenant ID
     */
    Optional<TableReservationDto> getTableReservationById(Long id, Long tenantId);
    
    /**
     * Get all table reservations for a tenant
     */
    List<TableReservationDto> getAllTableReservationsByTenant(Long tenantId);
    
    /**
     * Get table reservations by customer ID
     */
    List<TableReservationDto> getTableReservationsByCustomer(Long customerId, Long tenantId);
    
    /**
     * Get table reservations by table ID
     */
    List<TableReservationDto> getTableReservationsByTable(Long tableId, Long tenantId);
    
    /**
     * Get table reservations by status for a tenant
     */
    List<TableReservationDto> getTableReservationsByStatus(TableReservation.TableReservationStatus status, Long tenantId);
    
    /**
     * Get pending table reservations for a tenant
     */
    List<TableReservationDto> getPendingTableReservations(Long tenantId);
    
    /**
     * Update table reservation
     */
    Optional<TableReservationDto> updateTableReservation(Long id, TableReservationDto reservationDto);
    
    /**
     * Confirm table reservation
     */
    Optional<TableReservationDto> confirmTableReservation(Long id, Long tenantId);
    
    /**
     * Reject table reservation
     */
    Optional<TableReservationDto> rejectTableReservation(Long id, Long tenantId);
    
    /**
     * Cancel table reservation
     */
    Optional<TableReservationDto> cancelTableReservation(Long id, Long tenantId);
    
    /**
     * Complete table reservation (customer arrived)
     */
    Optional<TableReservationDto> completeTableReservation(Long id, Long tenantId);
    
    /**
     * Delete table reservation
     */
    boolean deleteTableReservation(Long id, Long tenantId);
    
    /**
     * Get table reservations by date range
     */
    List<TableReservationDto> getTableReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Long tenantId);
    
    /**
     * Get table reservations by customer ID with pagination
     */
    Page<TableReservationDto> getTableReservationsByCustomer(Long customerId, Long tenantId, Pageable pageable);
    
    /**
     * Check for overlapping reservations
     */
    boolean hasOverlappingReservations(Long tableId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Get table reservation count by status and tenant ID
     */
    long getTableReservationCountByStatus(TableReservation.TableReservationStatus status, Long tenantId);
    
    /**
     * Get total table reservation count by tenant ID
     */
    long getTableReservationCountByTenant(Long tenantId);
} 