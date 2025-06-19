package com.coworking.reservationsystem.repository;

import com.coworking.reservationsystem.model.entity.TableReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TableReservationRepository extends JpaRepository<TableReservation, Long> {
    
    /**
     * Find all table reservations by tenant ID
     */
    List<TableReservation> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    
    /**
     * Find all table reservations by customer ID
     */
    List<TableReservation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    /**
     * Find all table reservations by customer ID and tenant ID
     */
    List<TableReservation> findByCustomerIdAndTenantIdOrderByCreatedAtDesc(Long customerId, Long tenantId);
    
    /**
     * Find all table reservations by table ID
     */
    List<TableReservation> findByTableIdOrderByCreatedAtDesc(Long tableId);
    
    /**
     * Find all table reservations by table ID and tenant ID
     */
    List<TableReservation> findByTableIdAndTenantIdOrderByCreatedAtDesc(Long tableId, Long tenantId);
    
    /**
     * Find all table reservations by status and tenant ID
     */
    List<TableReservation> findByStatusAndTenantId(TableReservation.TableReservationStatus status, Long tenantId);
    
    /**
     * Find all pending table reservations by tenant ID
     */
    List<TableReservation> findByStatusAndTenantIdOrderByRequestedTimeAsc(TableReservation.TableReservationStatus status, Long tenantId);
    
    /**
     * Find table reservation by ID and tenant ID
     */
    Optional<TableReservation> findByIdAndTenantId(Long id, Long tenantId);
    
    /**
     * Find overlapping table reservations for a specific table and time range
     */
    @Query("SELECT tr FROM TableReservation tr WHERE tr.table.id = :tableId " +
           "AND tr.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((tr.requestedTime BETWEEN :startTime AND :endTime) " +
           "OR (tr.estimatedArrivalTime BETWEEN :startTime AND :endTime) " +
           "OR (:startTime BETWEEN tr.requestedTime AND tr.estimatedArrivalTime))")
    List<TableReservation> findOverlappingReservations(
            @Param("tableId") Long tableId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find table reservations by date range and tenant ID
     */
    @Query("SELECT tr FROM TableReservation tr WHERE tr.tenant.id = :tenantId " +
           "AND tr.requestedTime >= :startDate AND tr.requestedTime <= :endDate " +
           "ORDER BY tr.requestedTime ASC")
    List<TableReservation> findByDateRangeAndTenantId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("tenantId") Long tenantId
    );
    
    /**
     * Find table reservations by customer ID with pagination
     */
    Page<TableReservation> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Find table reservations by customer ID and tenant ID with pagination
     */
    Page<TableReservation> findByCustomerIdAndTenantId(Long customerId, Long tenantId, Pageable pageable);
    
    /**
     * Count table reservations by status and tenant ID
     */
    long countByStatusAndTenantId(TableReservation.TableReservationStatus status, Long tenantId);
    
    /**
     * Count table reservations by tenant ID
     */
    long countByTenantId(Long tenantId);
} 